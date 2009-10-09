package sonata.test.unit.loader;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import org.sonata.framework.common.AbstractInitializer;
import org.sonata.framework.common.InitializerDAO;
import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;

import sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2;


/*
 * TODO Modifier l'initializer de manière à enregistrer les propriétés,
 * dont font partie les classes techniques
 */
/*
 * TODO L'initializer réalise une partie de l'intégration entre Factory et
 * Invoker. On doit pouvoir factoriser la méthode register dès à présent !
 */
public class AbstractInitializerTest {

	private AbstractInitializer initializer ;
	private MyPropertiesLoader loader ;
	
	@Before
	public void setUp() throws Exception {
		loader = new MyPropertiesLoader() ;
		initializer = new AbstractInitializer(loader) ;
	}

	@Test
	public final void testLoadSymphonyObjects() {
		boolean hasThrownException = false ;
		loader.setSymphonyObject("sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2") ;
		
		try {
			initializer.loadSymphonyObjects() ;
		} catch (ClassNotFoundException e) {
			hasThrownException = true ;
			e.printStackTrace();
		}
		
		assertFalse(hasThrownException) ;
	}
	
	@Test
	public final void testBasicProperties() {
		boolean exceptionWasThrown = false ;
		// The following BDD approach is somewhat idiomatic, but hey, it helped!
		
		// As a user of the framework, I want to be able to define a set of properties,
		// which will be loaded at runtime into the corresponding Symphony Object instances
		
		// Given a set of properties
		loader.setProperty("SampleObject2.address", "\"221b Baker Street, London, England\"") ;
		
		// And a Symphony Object
		loader.setSymphonyObject("sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2") ;
		
		// When we first load the Symphony Objects
		try {
			initializer.loadSymphonyObjects() ;
		} catch (ClassNotFoundException e1) {
			exceptionWasThrown = true ;
			e1.printStackTrace();
		}
		assertFalse(exceptionWasThrown) ;
		
		// And we load the properties
		try {
			initializer.loadProperties() ;
		} catch (ClassNotFoundException e) {
			exceptionWasThrown = true ;
			e.printStackTrace();
		}
		assertFalse(exceptionWasThrown) ;
		
		// Then every set of properties should be associated with the right Symphony Object
		Properties objectProperties = initializer.getProperties("SampleObject2") ;
		assertTrue(objectProperties != null) ;
		assertEquals("\"221b Baker Street, London, England\"", objectProperties.getProperty("address")) ;
		
		// Additionally, when we setup the factory so that it loads all the Symphony Objects
		initializer.setupFactory() ;
		
		// Then when we create a Symphony Object entity
		SampleObject2 anObject = (SampleObject2) AbstractEntityFactory.getInstance().createEntity(SampleObject2.class) ;
		assertNotNull(anObject) ;
		
		// The properties should be set for every attribute that was set
		assertEquals("221b Baker Street, London, England", anObject.getAddress()) ;
	}

}


/*************************************************************
 * 
 * 		HERE BE UTILITY CLASSES
 *
/*************************************************************/
class MyPropertiesLoader extends InitializerDAO {
	
	public void setProperty(String name, String value) {
		this.theProperties.setProperty(name, value) ;
	}
	
	public void setSymphonyObject(String soName) {
		if (!soNames.contains(soName)) {
			soNames.add(soName) ;
		}
	}
}

/*************************************************************
 * 
 * 		HERE BE TEST CLASSES
 *
/*************************************************************/

//interface SampleObject2 {
//	void setAddress(String address) ;
//	String getAddress() ;
//}
//
//class SampleObject2Impl implements SampleObject2, EntityObject {
//	String address ;
//	
//	public SampleObject2Impl() {
//		
//	}
//	
//	@Override
//	public String getAddress() {
//		return address ;
//	}
//	
//	@Override
//	public void setAddress(String address) {
//		this.address = address ;
//	}
//}

