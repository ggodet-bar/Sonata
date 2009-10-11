package sonata.test.integration;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.AbstractInitializer;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.InitializerDAO;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.control.invoker.BrokerReference;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.control.invoker.InvokerDAO;
import org.sonata.framework.control.invoker.ReferenceElement;

import sonata.test.unit.abstractentityfactory.sampleobject.SampleObject;
import sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2;
import sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent;
import sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalImplementation;
import sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalInterface;

/*
 * 
 * TODO Should test the request getProxy method for correctness
 */
/**
 * This test validates the basic behaviours of Sonata, such as automatic connection 
 * between Symphony Object instances, configuration, the execution of Translation code
 * etc.
 * 
 * @author godetg
 *
 */
public class BasicSonataOperations {
	private Invoker theInvoker ;
	private ConnectionLoader loader ;
	private ReferenceElement sourceReference ;
	private ReferenceElement targetReference ;
	private ReferenceElement targetReference2 ;
	private Class<? extends ConnectionTranslation> translation ;
	private AbstractInitializer initializer ;
	private MyPropertiesLoader propLoader ;
	
	/*
	 * Used by the tests
	 */
	public static AbstractEntityFactory theFactory ;
	public static int aspectConnectionCounter;
	public static String address ;
	public static TechnicalInterface technicalComponent ;
	
	@Before
	public void setUp() throws Exception {
		theInvoker = MyInvoker.getInstance() ;
		theFactory = MyFactory.getInstance() ;
		theInvoker.setUnitTesting(true) ;
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		try {
			sourceReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.abstractentityfactory.sampleobject.SampleObject"), ReferenceType.IOE) ;
			targetReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2"), ReferenceType.BOE) ;
			targetReference2 = new ReferenceElement(classLoader.loadClass("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent"), ReferenceType.BOE) ;
			translation = (Class<? extends ConnectionTranslation>) classLoader.loadClass("sonata.test.integration.SampleObjectTranslation") ;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		loader = new ConnectionLoader() ;
		theInvoker.setDao(loader) ;
		
		propLoader = new MyPropertiesLoader() ;
		initializer = new AbstractInitializer(propLoader) ;
		
		propLoader.addSymphonyObject("sonata.test.unit.abstractentityfactory.sampleobject.SampleObject") ;
		propLoader.addSymphonyObject("sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2") ;
		propLoader.addSymphonyObject("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent") ;
		initializer.loadSymphonyObjects() ;
	}
	
	@After
	public void tearDown() throws Exception {
		MyInvoker.resetInstance() ;
		MyFactory.resetInstance() ;
		initializer =  null ;
		address = null ;
		technicalComponent = null ;
	}
	
	/**
	 * This method actually replicates the responsibility of a DAO
	 */
	private void loadConnection() {
		loader.addReference(sourceReference, translation, targetReference, targetReference2) ;
		try {
			theInvoker.loadConnections() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void shouldCaptureSimpleCreateEntityCalls(){
		initializer.setupFactory() ;
		loadConnection();
		aspectConnectionCounter = 0;
		assertEquals(0, theFactory.instances(SampleObject.class).size()) ;
		assertEquals(0, aspectConnectionCounter) ;
		
		
		SampleObject o1 = (SampleObject) theFactory.createEntity(SampleObject.class) ;
		assertEquals(1, aspectConnectionCounter) ;
		assertEquals(0, theFactory.instances(SampleObject2.class).size()) ;
		assertEquals(1, theFactory.instances(SampleObject.class).size()) ;
		o1.triggeringCall() ;
		
		assertEquals(1, theFactory.instances(SampleObject2.class).size()) ;
		assertEquals(1, theFactory.instances(SampleObject.class).size()) ;
		
		o1.triggeringCall() ;
		assertEquals(2, theFactory.instances(SampleObject2.class).size()) ;
		assertEquals(1, theFactory.instances(SampleObject.class).size()) ;
		assertEquals(1, aspectConnectionCounter) ;
	}
	
	@Test
	public void shouldWorkWithProperties() {
		propLoader.setProperty("SampleObject2.address", "\"221b Baker Street, London, England\"") ;
		try {
			initializer.loadProperties() ;
		} catch (ClassNotFoundException e) {
			fail("Exception was thrown!") ;
			e.printStackTrace();
		}
		initializer.setupFactory() ;
		loadConnection() ;
		
		
		SampleObject o1 = (SampleObject) theFactory.createEntity(SampleObject.class) ;
		assertNull(address) ;
		o1.triggeringCall() ;
		assertEquals(1, theFactory.instances(SampleObject2.class).size()) ;
		assertEquals("221b Baker Street, London, England", address) ;
	}
	
	@Test
	public void shouldWorkWithTechnicalComponents() {
		propLoader.addTechnicalInterface("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalImplementation") ;

		List<String> techComponents = new ArrayList<String>() ;
		techComponents.add("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalImplementation") ;
		
		propLoader.setTechnicalConnection("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent", techComponents) ;
		
		try {
			initializer.loadTechnicalComponents() ;
		} catch (ClassNotFoundException e) {
			fail("Exception was thrown!") ;
			e.printStackTrace();
		}
		assertTrue(initializer.getTechnicalComponentClasses().contains(TechnicalImplementation.class)) ;
		
		initializer.setupFactory() ;
		loadConnection() ;
		assertTrue(theFactory.instances(SampleObjectWithTechnicalComponent.class).isEmpty()) ;
		
		SampleObject o1 = (SampleObject) theFactory.createEntity(SampleObject.class) ;
		assertTrue(theFactory.instances(SampleObjectWithTechnicalComponent.class).isEmpty()) ;
		assertNull(technicalComponent) ;
		
		o1.triggerTechnicalCall() ;
		assertEquals(1, theFactory.instances(SampleObjectWithTechnicalComponent.class).size()) ;
		assertNotNull(technicalComponent) ;
		assertTrue(technicalComponent instanceof TechnicalImplementation) ;
	}
	
	@Test
	public void shouldWorkWithPropertiesAndTechnicalComponents() {
		propLoader.addTechnicalInterface("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalImplementation") ;

		List<String> techComponents = new ArrayList<String>() ;
		techComponents.add("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.TechnicalImplementation") ;
		
		propLoader.setTechnicalConnection("sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent", techComponents) ;
		propLoader.setProperty("SampleObject2.address", "\"221b Baker Street, London, England\"") ;
		
		try {
			initializer.loadProperties() ;
		} catch (ClassNotFoundException e) {
			fail("Exception was thrown!") ;
			e.printStackTrace();
		}
		
		try {
			initializer.loadTechnicalComponents() ;
		} catch (ClassNotFoundException e) {
			fail("Exception was thrown!") ;
			e.printStackTrace();
		}
		
		initializer.setupFactory() ;
		loadConnection() ;
		
		assertNull(address) ;
		assertNull(technicalComponent) ;
		
		SampleObject o1 = (SampleObject) theFactory.createEntity(SampleObject.class) ;
		o1.triggerBothCall() ;
		
		assertNotNull(address) ;
		assertNotNull(technicalComponent) ;
		assertEquals("221b Baker Street, London, England", address) ;
		assertTrue(technicalComponent instanceof TechnicalImplementation) ;
		
		
	}
}
/*************************************************************
 * 
 * 		HERE BE UTILITY CLASSES
 *
/*************************************************************/

class MyInvoker extends Invoker {
	public static void resetInstance() {
		instance = null ;
	}
}

class MyFactory extends AbstractEntityFactory {
	public static void resetInstance() {
		instance = null ;
	}
}

class ConnectionLoader extends InvokerDAO {
	
	public void addReference(ReferenceElement sourceReference, Class<? extends ConnectionTranslation> translation, ReferenceElement...destinations) {
		BrokerReference aReference = new BrokerReference() ;
		aReference.setSource(sourceReference) ;
		for (ReferenceElement aDestination : destinations) {
			aReference.addDestination(aDestination) ;
		}
		aReference.setTranslation(translation) ;
		
		theReferences.add(aReference) ;
	}
	
}

class MyPropertiesLoader extends InitializerDAO {
	
	public void setProperty(String name, String value) {
		theProperties.setProperty(name, value) ;
	}

	public void setTechnicalConnection(String objectName,
			List<String> techComponents) {
		technicalConnections.put(objectName, techComponents) ;
		
	}

	public void addSymphonyObject(String soName) {
		if (!soNames.contains(soName)) {
			soNames.add(soName) ;
		}
	}
	
	public void addTechnicalInterface(String interfaceName) {
		technicalComponents.add(interfaceName) ;
	}
}


