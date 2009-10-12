package sonata.test.unit.invoker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.control.exceptions.RequestOverlapException;
import org.sonata.framework.control.invoker.BrokerReference;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.control.invoker.InvokerDAO;
import org.sonata.framework.control.invoker.ReferenceElement;
import org.sonata.framework.control.invoker.Request;

public class InvokerTest extends TestCase {
	
	/*
	 * The Invoker is subclassed in order to bypass the singleton mechanism (constructor is only protected)
	 */
	private class myInvoker extends Invoker {
		public boolean registerConnection(BrokerReference reference) {
			return super.registerConnection(reference) ;
		}
	}
	
	private myInvoker	theInvoker ;
	private ConnectionLoader loader ;
	private ReferenceElement sourceReference ;
	private ReferenceElement targetReference ;
	private Class<? extends ConnectionTranslation> translation ;
	

	@Before
	public void setUp() throws Exception {
		theInvoker = new myInvoker() ;
		theInvoker.setUnitTesting(true) ;
		
		// Connections should be configured by the Initializer, which
		// should also be responsible for loading the data from files.
		// Therefore, the following code should not be manipulated by
		// users. It is included there for guaranteeing maximum 
		// decoupling between tested classes
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		try {
			sourceReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.invoker.SampleObject"), ReferenceType.IOE) ;
			targetReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.invoker.SampleObject2"), ReferenceType.BOE) ;
			translation = (Class<? extends ConnectionTranslation>) classLoader.loadClass("sonata.test.unit.invoker.SampleObjectTranslation") ;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		loader = new ConnectionLoader() ;
		theInvoker.setDao(loader) ;
	}
	
	/**
	 * This method actually replicates the responsibility of a DAO
	 */
	private void loadConnection() {
		loader.addReference(sourceReference, translation, targetReference) ;
		try {
			theInvoker.loadConnections() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		theInvoker = null ;
	}

	@Test
	public final void testRegisterConnection() {	
		
		int nbConnections = 0;
		loader.addReference(sourceReference, translation, targetReference) ;
		try {
			nbConnections = theInvoker.loadConnections();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(1, nbConnections) ;
	}
	
	@Test
	public final void testDoNotRegisterConnectionTwice() {
		boolean exceptionWasThrown = false ;
		loader.addReference(sourceReference, translation, targetReference) ;
		loader.addReference(sourceReference, translation, targetReference) ;
		try {
			theInvoker.loadConnections() ;
		} catch (IOException e) {
			exceptionWasThrown = true ;
		}
		assertTrue(exceptionWasThrown) ;
	}
	
	@Test
	public final void testDontBindEmptyConnections() {
		loadConnection() ;
		
		// The Invoker should bin valid objects
		SampleObject sample = new SampleObjectImpl() ;
		boolean isObjectBounded = theInvoker.bind((SymphonyObject) sample) ;
		assertFalse(isObjectBounded) ;
		
		SampleObject2 sample2 = new SampleObject2Impl() ;
		isObjectBounded = theInvoker.bind((SymphonyObject) sample2) ;
		assertFalse(isObjectBounded) ;
		
		// Note that, even though both objects are described as part of a connection,
		// the binding occurs only if a SampleObject2 is created in the context of 
		// a request (see integration tests)!

	}
	
	
	@Test
	public final void testCreateRequest() {
		Request req ;
		
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl() ;
		theInvoker.bind((SymphonyObject) sample) ;
		try {
			req = theInvoker.createRequest((EntityObject)sample, "translateCall") ;
			assertNotNull(req) ;
			

			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertTrue(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public final void  testShouldThrowRequestOverlapException() {
		boolean exceptionThrown = false ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl() ;
		theInvoker.bind((SymphonyObject) sample) ;
		try {
			theInvoker.createRequest((EntityObject)sample, "translateCall") ;
			theInvoker.createRequest((EntityObject)sample, "translateCall") ;
			
		} catch (RequestOverlapException e) {
			exceptionThrown = true ;
			//e.printStackTrace();
		}
		
		assertTrue(exceptionThrown) ;
	}
	
	@Test
	public final void testRequestWithUniformParameters() {
		Request req ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl() ;
		theInvoker.bind((SymphonyObject) sample) ;
		
		try {
			req = theInvoker.createRequest((EntityObject)sample, "callWithParameters") ;
			req.pushParameter(9) ;
			req.pushParameter("aString") ;
			
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertTrue(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			fail("Exception was thrown!");
			e.printStackTrace() ;
		} catch (NoSuchMethodException e) {
			fail("Exception was thrown!");
			e.printStackTrace() ;
		} catch (InvocationTargetException e) {
			fail("Exception was thrown!");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Translation methods with a mix of wrapped and unwrapped primitive types
	 * are not supported
	 */
	@Test
	public final void testRequestWithNonUniformParameters() {
		boolean exceptionThrown = false ;
		Request req ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl() ;
		theInvoker.bind((SymphonyObject) sample) ;
		
		try {
			req = theInvoker.createRequest((EntityObject)sample, "callWithNonUniformParameters") ;
			req.pushParameter(new Integer(9)) ;
			req.pushParameter(10) ;
			req.pushParameter(new Long(11L)) ;
			req.pushParameter(new Boolean("true")) ;
			req.pushParameter(false) ;
			req.pushParameter("aString") ;
			
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertTrue(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			fail("Wrong exception was thrown!");
		} catch (NoSuchMethodException e) {
			exceptionThrown = true ;
			e.printStackTrace() ;
		} catch (InvocationTargetException e) {
			fail("Wrong exception was thrown!");
			e.printStackTrace();
		}
		
		assertTrue(exceptionThrown) ;
	}
	
	@Test
	public final void testShouldCatchExceptionsSentFromTranslation() {
		boolean isExceptionThrown = false ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl() ;
		theInvoker.bind((SymphonyObject) sample) ;
		
		try {
			theInvoker.createRequest((EntityObject)sample, "throwException") ;
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertFalse(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			fail("Wrong exception was thrown!");
			e.printStackTrace() ;
		} catch (NoSuchMethodException e) {
			fail("Wrong exception was thrown!");
			e.printStackTrace() ;
		} catch (InvocationTargetException e) {
			isExceptionThrown = true ;
			assertEquals("Sample exception", e.getCause().getMessage()) ;
			e.printStackTrace() ;
		}
		
		assertTrue(isExceptionThrown) ;
	}
}

/*************************************************************
 * 
 * 		HERE BE UTILITY CLASSES
 *
/*************************************************************/

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

/*************************************************************
 * 
 * 		HERE BE TEST CLASSES
 *
/*************************************************************/
class AnyObject implements SymphonyObject {
	
}

///////////////////////////////////////////////////////////////

interface SampleObject2 {
	String address() ;
}

class SampleObject2Impl implements SampleObject2, EntityObject {
	String address ;
	
	@Override
	public String address() {
		return address ;
	}

}

///////////////////////////////////////////////////////////////

interface SampleObject {
	String username();

	void triggeringCall() ;
}

class SampleObjectImpl implements SampleObject, EntityObject {
	private String username ;
	
	public String username() {
		return username ;
	}

	@Override
	public void triggeringCall() {
		
	}
}
