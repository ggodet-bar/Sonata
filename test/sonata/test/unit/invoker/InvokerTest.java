package sonata.test.unit.invoker;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

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
import org.sonata.framework.control.invoker.ReferenceElement;
import org.sonata.framework.control.request.Request;

public class InvokerTest extends TestCase {
	
	/*
	 * The Invoker is subclassed in order to bypass the singleton mechanism (constructor is only protected)
	 */
	private class myInvoker extends Invoker {}
	
	private Invoker	theInvoker ;
	ReferenceElement sourceReference ;
	ReferenceElement targetReference ;
	Class<? extends ConnectionTranslation> translation ;
	

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
			sourceReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.invoker.SampleObject"), ReferenceType.OIE) ;
			targetReference = new ReferenceElement(classLoader.loadClass("sonata.test.unit.invoker.SampleObject2"), ReferenceType.OME) ;
			translation = (Class<? extends ConnectionTranslation>) classLoader.loadClass("sonata.test.unit.invoker.SampleObjectTranslation") ;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method actually replicates the responsibility of the DAO
	 */
	private void loadConnection() {
		BrokerReference aReference = new BrokerReference() ;
		aReference.setSource(sourceReference) ;
		aReference.addDestination(targetReference) ;
		aReference.setTranslation(translation) ;
		
		theInvoker.registerConnection(aReference) ;
	}
	
	@After
	public void tearDown() throws Exception {
		theInvoker = null ;
	}

	@Test
	public final void testRegisterConnection() {	
		BrokerReference aReference = new BrokerReference() ;
		aReference.setSource(sourceReference) ;
		aReference.addDestination(targetReference) ;
		aReference.setTranslation(translation) ;
		boolean didRegister = theInvoker.registerConnection(aReference) ;
		
		assertTrue(didRegister) ;
	}
	
	@Test
	public final void testDoNotRegisterConnectionTwice() {
		BrokerReference aReference = new BrokerReference() ;
		aReference.setSource(sourceReference) ;
		aReference.addDestination(targetReference) ;
		aReference.setTranslation(translation) ;
		
		theInvoker.registerConnection(aReference) ;
		boolean didRegister = theInvoker.registerConnection(aReference) ;
		
		assertFalse(didRegister) ;
	}
	
	@Test
	public final void testRegisterValidObjects() {
		loadConnection() ;
		
		// The Invoker should register valid objects
		SampleObject sample = new SampleObjectImpl(null) ;
		boolean isObjectRegistered = theInvoker.register((SymphonyObject) sample) ;
		assertTrue(isObjectRegistered) ;
		
		SampleObject2 sample2 = new SampleObject2Impl(null) ;
		isObjectRegistered = theInvoker.register((SymphonyObject) sample2) ;
		assertTrue(isObjectRegistered) ;
		
		// Note that, even though both objects are described as part of a connection,
		// the binding occurs only if a SampleObject2 is created in the context of 
		// a request (see integration tests)!

	}
	
	@Test
	public final void testRegisterIncorrectlyStructuredObject() {
		loadConnection() ;

		AnyObject anyObject = new AnyObject() ;
		boolean isObjectRegistered = theInvoker.register(anyObject) ;
		assertFalse(isObjectRegistered) ;
	}
	
	@Test
	public final void testShouldNotRegisterSameObjectTwice() {
		loadConnection() ;

		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		
		boolean isObjectRegistered = theInvoker.register((SymphonyObject) sample) ;
		assertFalse(isObjectRegistered) ;
	}
	
	
	
	@Test
	public final void testCreateRequest() {
		Request req ;
		
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		try {
			req = theInvoker.createRequest((EntityObject)sample, "translateCall", null) ;
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
		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		try {
			theInvoker.createRequest((EntityObject)sample, "translateCall", null) ;
			theInvoker.createRequest((EntityObject)sample, "translateCall", null) ;
			
		} catch (RequestOverlapException e) {
			exceptionThrown = true ;
			//e.printStackTrace();
		}
		
		assertTrue(exceptionThrown) ;
	}
	
	@Test
	public final void testRequestWithUniformParameters() {
		boolean exceptionNotThrown = true ;
		Request req ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		
		try {
			req = theInvoker.createRequest((EntityObject)sample, "callWithParameters", null) ;
			req.pushParameter(9) ;
			req.pushParameter("aString") ;
			
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertTrue(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			exceptionNotThrown = false ;
			e.printStackTrace() ;
		} catch (NoSuchMethodException e) {
			exceptionNotThrown = false ;
			e.printStackTrace() ;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(exceptionNotThrown) ;
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
		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		
		try {
			req = theInvoker.createRequest((EntityObject)sample, "callWithNonUniformParameters", null) ;
			req.pushParameter(new Integer(9)) ;
			req.pushParameter(10) ;
			req.pushParameter(new Long(11L)) ;
			req.pushParameter(new Boolean("true")) ;
			req.pushParameter(false) ;
			req.pushParameter("aString") ;
			
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertTrue(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			e.printStackTrace() ;
		} catch (NoSuchMethodException e) {
			exceptionThrown = true ;
			e.printStackTrace() ;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(exceptionThrown) ;
	}
	
	@Test
	public final void testShouldCatchExceptionsSentFromTranslation() {
		boolean isExceptionThrown = false ;
		loadConnection() ;
		SampleObject sample = new SampleObjectImpl(null) ;
		theInvoker.register((SymphonyObject) sample) ;
		
		try {
			theInvoker.createRequest((EntityObject)sample, "throwException", null) ;
			boolean requestDidSucceed = theInvoker.sendRequest() ;
			assertFalse(requestDidSucceed) ;
		} catch (RequestOverlapException e) {
			e.printStackTrace() ;
		} catch (NoSuchMethodException e) {
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
 * 		HERE BE TEST CLASSES
 *
/*************************************************************/
/*
 * TODO This file should be moved to the DAO test!!
 */
interface XMLFile {
	
	String theFile =
		"<?xml version='1.0' encoding='UTF-8'?>\n" +
		"<SOConnections>\n" +
		"	<SOConnection>\n" +
		"		<source type='OIE' name='sonata.test.sampleobject.SampleObject' />\n" +
		"		<destination type='OME' name='sonata.test.sampleobject2.SampleObject2' />\n" +
		"		<translation name='sonata.test.translation.SampleObjectTranslation' />\n" +
		"	</SOConnection>\n" +
		"</SOConnections>\n" ;
}

class AnyObject implements SymphonyObject {
	
}

///////////////////////////////////////////////////////////////

interface SampleObject2 {
	String address() ;
}

class SampleObject2Impl implements SampleObject2, EntityObject {
	String address ;
	
	public SampleObject2Impl(Properties prop) {
		if (prop != null) {
			address = prop.getProperty("address") ;
		}
	}
	
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
