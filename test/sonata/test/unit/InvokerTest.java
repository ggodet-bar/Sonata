package sonata.test.unit;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.common.entity.EntityObject;

public class InvokerTest extends TestCase {
	
	private Invoker	theInvoker ;

	@Before
	public void setUp() throws Exception {
		theInvoker = Invoker.instance ;
		theInvoker.setXMLFilePath("./test/sonata/SOConnections.xml") ;
		theInvoker.setUnitTesting(true) ;
	}
	
	@After
	public void tearDown() throws Exception {
		theInvoker = null ;
	}

	@Test
	public final void testCreateRequest() {
		//fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRegister() {

		int nbConnections = theInvoker.loadConnections() ;
		assert(nbConnections == 1) ;
		
		// The Invoker should register valid objects
		SampleObject sample = new SampleObjectImpl(null) ;
		boolean isObjectRegistered = Invoker.instance.register((SymphonyObject) sample) ;
		assert(isObjectRegistered) ;
		
		// ... but it should reject incorrectly structured objects
		AnyObject anyObject = new AnyObject() ;
		isObjectRegistered = Invoker.instance.register(anyObject) ;
		assert(!isObjectRegistered) ;
	}
	
	@Test
	public final void testRequestWithParameter() {
		
	}
	
	/*************************************************************
	 * 
	 * 		HERE BE TEST CLASSES
	 *
	/*************************************************************/
	private class AnyObject implements SymphonyObject {
		
	}
	
	private interface SampleObject {
		String username();

		void triggeringCall() ;
	}
	
	private class SampleObjectImpl implements SampleObject, EntityObject {
		private String username ;
		
		public SampleObjectImpl(Properties prop) {
			if (prop != null) {
				username = prop.getProperty("username") ;
			}
		}
		
		public String username() {
			return username ;
		}

		@Override
		public void triggeringCall() {
			
		}
	}

}
