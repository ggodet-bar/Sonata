package sonata.test.unit.abstractentityfactory;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObjectServices;


public class AbstractEntityFactoryTest {
	
	private AbstractEntityFactory	aFactory ;
	
	/**
	 * @precondition
	 * 		1.	The factory is instantiated
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Using an anonymous subclass for bypassing the singleton mechanism
		aFactory = new AbstractEntityFactory() {};
		
	}

	@After
	public void tearDown() throws Exception {
		aFactory = null ;
	}
	
	/**
	 * @scenario 
	 * 		1.	When at startup phase, the system registers a new SampleObject
	 * class within the factory. A list of properties is also transferred, with the 
	 * following elements :<br />
	 * 			<table>
	 * 				<tr style="font-weight:bold">
	 * 					<td>Property</td><td>Value</td>
	 * 				</tr>
	 * 				<tr>
	 * 					<td>username</td><td>Bob</td>
	 * 				</tr>
	 * 			</table>
	 * 		2.	Then an instance of the sample object should integrate the properties
	 * 		3.	And searching for the object should return the same reference
	 */
	@Test
	public void shouldRegisterEntityObject() {
		Properties prop = new Properties() ;
		prop.setProperty("username", "Bob") ;
		boolean isRegistered = aFactory.register(SampleObject.class, prop) ;
		assertTrue(isRegistered) ;
		
		SampleObject sample = (SampleObject) aFactory.createEntity(SampleObject.class) ;
		assertNotNull (sample) ;
		assertEquals ("Bob", sample.getUsername()) ;
		
		int oID = ((EntityObjectServices)sample).getID() ;
		SampleObject object = (SampleObject) aFactory.search(SampleObject.class, oID) ;
		
		assertEquals(object, sample) ;
 	}
	
	@Test
	public void shouldRegisterTwoEntityObjects() {
		Properties prop1 = new Properties(), prop2 = new Properties() ;
		prop1.setProperty("username", "Albert") ;
		prop2.setProperty("address", "\"Wall Street, NYC, United States of America\"") ;
		aFactory.register(SampleObject.class, prop1) ;
		boolean isRegistered = aFactory.register(SampleObject2.class, prop2) ;
		assertTrue(isRegistered) ;

		SampleObject sample = (SampleObject) aFactory.createEntity(SampleObject.class) ;
		assertNotNull (sample) ;
		assertEquals ("Albert", sample.getUsername()) ;
		
		SampleObject2 sample2 = (SampleObject2) aFactory.createEntity(SampleObject2.class) ;
		assertNotNull (sample2) ;
		assertEquals ("Wall Street, NYC, United States of America", sample2.getAddress()) ;
	}
	
	@Test
	public void shouldSupportPrimitiveTypeProperties() {
		Properties prop1 = new Properties() ;
		prop1.setProperty("username", "Albert") ;
		prop1.setProperty("age", "22") ;
		prop1.setProperty("male", "true") ;
		aFactory.register(SampleObject.class, prop1) ;
		SampleObject sample = (SampleObject) aFactory.createEntity(SampleObject.class) ;
		
		assertNotNull (sample) ;
		assertEquals(22, sample.getAge()) ;
		assertEquals(true, sample.isMale()) ;
	}
	
	@Test
	public void shouldSupportComplexTypeProperties() {
		Properties prop1 = new Properties() ;
		prop1.setProperty("flatDimensions", "23, 34") ;
		aFactory.register(SampleObject.class, prop1) ;
		SampleObject sample = (SampleObject) aFactory.createEntity(SampleObject.class) ;
		assertNotNull (sample) ;
		assertEquals(new Dimension(23, 34), sample.getFlatDimensions()) ;
	}
	
	@Test
	public void shouldSupportConcurrentAccess() {
		Properties prop1 = new Properties(), prop2 = new Properties() ;
		prop1.setProperty("username", "Albert") ;
		prop2.setProperty("address", "\"Wall Street, NYC, United States of America\"") ;
		aFactory.register(SampleObject.class, prop1) ;
		aFactory.register(SampleObject2.class, prop2) ;
		
		
		// We create 2000 instances for each object, which try to access the factory
		// simultaneously
		
		Thread createObj1 = new Thread() {
			
			public void run() {
				for (int i = 0 ; i < 2000 ; i++) {
					aFactory.createEntity(SampleObject.class) ;
				}
			}
		} ;
		
		Thread createObj2 = new Thread() {
			
			public void run() {
				for (int i = 0 ; i < 2000 ; i++) {
					aFactory.createEntity(SampleObject2.class) ;
				}
			}
		} ; 
		
		createObj1.start() ;
		createObj2.start() ;
		
		try {
			createObj1.join() ;
			createObj2.join() ;

			
			assertEquals(2000, aFactory.instances(SampleObject.class).size()) ;
			assertEquals(2000, aFactory.instances(SampleObject2.class).size()) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void shouldDeleteObjectsInRightFactory() {
		Properties prop1 = new Properties(), prop2 = new Properties() ;
		prop1.setProperty("username", "Albert") ;
		prop2.setProperty("address", "\"Wall Street, NYC, United States of America\"") ;
		aFactory.register(SampleObject.class, prop1) ;
		aFactory.register(SampleObject2.class, prop2) ;
		
		for (int i = 0 ; i < 2000 ; i++) {
			aFactory.createEntity(SampleObject.class) ;
		}
		
		for (int i = 0 ; i < 2000 ; i++) {
			aFactory.createEntity(SampleObject2.class) ;
		}
		
		boolean deletionDone = aFactory.delete(SampleObject.class, 999) ;
		
		assertTrue(deletionDone) ;
		assertEquals(1999, aFactory.instances(SampleObject.class).size()) ;
		assertEquals(2000, aFactory.instances(SampleObject2.class).size()) ;
		
	}
}


