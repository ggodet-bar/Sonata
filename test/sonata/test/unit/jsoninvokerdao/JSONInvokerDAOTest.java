package sonata.test.unit.jsoninvokerdao;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.invoker.BrokerReference;
import org.sonata.framework.control.invoker.JSONInvokerDAO;
import org.sonata.framework.control.invoker.ReferenceElement;


public class JSONInvokerDAOTest {

	JSONInvokerDAO loader ;
	
	@Before
	public void setUp() throws Exception {
		loader = new JSONInvokerDAO() ;
	}
	
	@After
	public void tearDown() throws Exception {
		loader = null ;
	}
	
	@Test
	public void testShouldLoadValidJsonText() {
		boolean exceptionNotThrown = true ;
		try {
			loader.parseJSONText(JSONText.theValidJSONString) ;
		} catch (JSONException e) {
			exceptionNotThrown = false ;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			exceptionNotThrown = false ;
			e.printStackTrace();
		}
		
		assertTrue(exceptionNotThrown) ;
		
		List<BrokerReference> references = loader.getBrokerReferences() ;
		
		int nbReferences = references.size() ;
		assertEquals(1, nbReferences) ;
		
		assertEquals(new ReferenceElement(SampleObject.class, ReferenceType.IOE), getFirstSource(references)) ;
		assertEquals(new ReferenceElement(SampleObject2.class, ReferenceType.BOE), getFirstDestination(references)) ;
		assertEquals(SampleObjectTranslation.class, getFirstTranslation(references)) ;
	}
	
	@Test
	public void testShouldLoadJSONFromFile() {
		File f = null ;
		PrintWriter pw ;
		boolean exceptionNotThrown = true ;

		try {
			f = File.createTempFile("SonataTest", ".json") ;
			pw = new PrintWriter(f) ;
			pw.write(JSONText.theValidJSONString) ;
			pw.close() ;
		} catch (IOException e) {
			exceptionNotThrown = false ;
			e.printStackTrace();
			return ;
		}
		
		try {
			loader.loadJSONFile(f.getAbsolutePath()) ;
		} catch (Exception e) {
			exceptionNotThrown = false ;
			e.printStackTrace();
		} 
		
		assertTrue(exceptionNotThrown) ;
		
		List<BrokerReference> references = loader.getBrokerReferences() ;
		
		int nbReferences = references.size() ;
		assertEquals(1, nbReferences) ;
		
		assertEquals(new ReferenceElement(SampleObject.class, ReferenceType.IOE), getFirstSource(references)) ;
		assertEquals(new ReferenceElement(SampleObject2.class, ReferenceType.BOE), getFirstDestination(references)) ;
		assertEquals(SampleObjectTranslation.class, getFirstTranslation(references)) ;
	}
	
	private ReferenceElement getFirstSource(List<BrokerReference> list) {
		return list.get(0).getSource() ;
	}
	
	private ReferenceElement getFirstDestination(List<BrokerReference> list) {
		return list.get(0).getDestinations().get(0) ;
	}
	
	private Class<? extends ConnectionTranslation> getFirstTranslation(List<BrokerReference> list) {
		return list.get(0).getTranslation() ;
	}
}

/*************************************************************
 * 
 * 		HERE BE TEST CLASSES
 *
/*************************************************************/
interface JSONText {
	
	String theValidJSONString =
		"{'SOConnections' : [" +
				"{'source' : " +
					"{'name' : 'sonata.test.unit.jsoninvokerdao.SampleObject', 'type': 'IOE'}," +
				"'destinations' : [ " +
					"{'name' : 'sonata.test.unit.jsoninvokerdao.SampleObject2', 'type': 'BOE'}" +
					"]," +
				"'translation' : 'sonata.test.unit.jsoninvokerdao.SampleObjectTranslation'" +
				"}"	+
		"]}" ;

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

///////////////////////////////////////////////////////////////

class SampleObjectTranslation extends ConnectionTranslation {

	public SampleObjectTranslation(SymphonyObject source, ProcessObject proxy) {
		super(source, proxy);
		// TODO Auto-generated constructor stub
	}
	
}