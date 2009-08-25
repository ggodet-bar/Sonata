package sonata.test.unit.invokerdao;


import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.exceptions.ParsingException;
import org.sonata.framework.control.invoker.BrokerReference;
import org.sonata.framework.control.invoker.ReferenceElement;
import org.sonata.framework.control.invoker.XMLInvokerDAO;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;

public class XMLInvokerDAOTest {

	File f ;
	XMLInvokerDAO loader ;
	
	@Before
	public void setUp() throws Exception {
		f = new File("SOConnections.xml") ;
		PrintWriter pw = new PrintWriter(f) ;
		pw.write(XMLFile.theValidFile) ;
		pw.close() ;
		
		loader = new XMLInvokerDAO() ;
	}
	
	@After
	public void tearDown() throws Exception {
		f.delete() ;
		loader = null ;
	}
	
	@Test
	public final void testParsing() {
		boolean parseResult = false ;
		boolean exceptionNotThrown = true ;
		try {
			parseResult = loader.parseXmlFile() ;
		} catch (ParsingException e) {
			exceptionNotThrown = false ;
			e.printStackTrace();
		}
		assertTrue(exceptionNotThrown) ;
		assertTrue(parseResult) ;
		
		List<BrokerReference> references = loader.getBrokerReferences() ;
		
		int nbReferences = references.size() ;
		assertEquals(1, nbReferences) ;
		
		assertEquals(new ReferenceElement(SampleObject.class, ReferenceType.OIE), getFirstSource(references)) ;
		assertEquals(new ReferenceElement(SampleObject2.class, ReferenceType.OME), getFirstDestination(references)) ;
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
interface XMLFile {
	
	String theValidFile =
		"<?xml version='1.0' encoding='UTF-8'?>\n" +
		"<SOConnections xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
			" xsi:schemaLocation='file://Schemas/SOConnections.xsd'>\n" +
		"	<SOConnection>\n" +
		"		<source type='OIE' name='sonata.test.unit.invokerdao.SampleObject' />\n" +
		"		<destination type='OME' name='sonata.test.unit.invokerdao.SampleObject2' />\n" +
		"		<translation name='sonata.test.unit.invokerdao.SampleObjectTranslation' />\n" +
		"	</SOConnection>\n" +
		"</SOConnections>\n" ;

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