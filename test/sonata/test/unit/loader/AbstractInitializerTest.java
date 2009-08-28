package sonata.test.unit.loader;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import org.sonata.framework.common.AbstractInitializer;
import org.sonata.framework.common.InitializerDAO;
import org.sonata.framework.common.entity.EntityObject;


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
	public final void testBasicProperties() {
		// The following BDD approach is somewhat idiomatic, but hey, it helped!
		
		// As a user of the framework, I want to be able to define a set of properties,
		// which will be loaded at runtime into the corresponding Symphony Object instances
		
		// Given a set of properties
		loader.setProperty("SampleObject2.address", "221b Baker Street, London, England") ;
		
		// When I load the properties into the initializer
		boolean didLoadProperty = initializer.loadProperties() ;
		assertTrue(didLoadProperty) ;
		
		// And I call the AbstractFactory for loading the list of connections and Symphony Objects,
		// using the properties that were parsed by the initializer
		
		
		// Then the SO declarations should be used for registration with the AbstractFactory
		
		// And the SO connections should be used for setting up the Invoker
		
	}

}
class MyPropertiesLoader extends InitializerDAO {
	
	public void setProperty(String name, String value) {
		this.theProperties.setProperty(name, value) ;
	}
}

/*************************************************************
 * 
 * 		HERE BE TEST CLASSES
 *
/*************************************************************/

interface SampleObject2 {
	void setAddress(String address) ;
	String getAddress() ;
}

class SampleObject2Impl implements SampleObject2, EntityObject {
	String address ;
	
	public SampleObject2Impl(Properties prop) {
		if (prop != null) {
			address = prop.getProperty("address") ;
		}
	}
	
	@Override
	public String getAddress() {
		return address ;
	}
	
	@Override
	public void setAddress(String address) {
		this.address = address ;
	}
}

