package sonata.test.unit;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.control.invoker.Invoker;

import sonata.test.sampleobject.SampleObject;
import sonata.test.sampleobject2.SampleObject2;

public class InvokerTest extends TestCase {
	
	private Invoker	theInvoker ;
	private AbstractEntityFactory theFactory ;

	/**
	 * When instantiated, the invoker immediately loads the list
	 * of SO connections. That should be either tested or extracted into
	 * a separate method!
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		theInvoker = Invoker.instance ;
		theFactory = new AbstractEntityFactory() ;
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
		Properties prop = new Properties() ;
		theFactory.register(SampleObject.class, prop) ;
		theFactory.register(SampleObject2.class, prop) ;
		SampleObject sample = (SampleObject) theFactory.createEntity(SampleObject.class) ;
		sample.triggeringCall() ;
		//fail("Not yet implemented"); // TODO
	}
	
	@Test
	public final void testRequestWithParameter() {
		
	}

}
