package sample.test.testprocessobjects;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.AbstractInitializer;
import org.sonata.framework.common.InitializerDAO;
import org.sonata.framework.common.process.AbstractProcessFactory;

import sonata.test.sampleprocess.SampleProcess;
import sonata.test.sampleprocess.TechnicalImplementation;

public class ProcessObjectTest {

	@Before
	public void setUp() throws Exception {
		
	}


	
	@Test
	public final void shouldExposeTechnicalComponent() {
		MyPropertiesLoader propLoader = new MyPropertiesLoader() ;
		propLoader.setSymphonyObject("sonata.test.sampleprocess.SampleProcess") ;
		propLoader.addTechnicalInterface("sonata.test.sampleprocess.TechnicalImplementation") ;
		
		List<String> techComponents = new ArrayList<String>() ;
		techComponents.add("sonata.test.sampleprocess.TechnicalImplementation") ;
		
		propLoader.setTechnicalConnection("sonata.test.sampleprocess.SampleProcess", techComponents) ;
		

		
		AbstractInitializer initializer = new AbstractInitializer(propLoader) ;
		
		try {
			initializer.loadSymphonyObjects() ;
			initializer.loadTechnicalComponents() ;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		initializer.setupFactory() ;
		
		SampleProcess aProcess = (SampleProcess) AbstractProcessFactory.getInstance().createProcess(SampleProcess.class) ;
		
		assertNotNull(aProcess.exposeTechnicalComponent()) ;
		assertTrue(aProcess.exposeTechnicalComponent() instanceof TechnicalImplementation) ;
	}
	
	@Test
	public final void shouldBehaveAsSingleton() {
		SampleProcess aProcess = (SampleProcess) AbstractProcessFactory.getInstance().createProcess(SampleProcess.class) ;
		SampleProcess anotherProcess = (SampleProcess) AbstractProcessFactory.getInstance().createProcess(SampleProcess.class) ;
			
		assertTrue(aProcess == anotherProcess) ;
	}
}

/*************************************************************
 * 
 * 		HERE BE UTILITY CLASSES
 *
/*************************************************************/
class MyPropertiesLoader extends InitializerDAO {
		
	public void setProperty(String name, String value) {
		theProperties.setProperty(name, value) ;
	}

	public void setTechnicalConnection(String objectName,
			List<String> techComponents) {
		technicalConnections.put(objectName, techComponents) ;
		
	}

	public void setSymphonyObject(String soName) {
		if (!soNames.contains(soName)) {
			soNames.add(soName) ;
		}
	}
	
	public void addTechnicalInterface(String interfaceName) {
		technicalComponents.add(interfaceName) ;
	}
}