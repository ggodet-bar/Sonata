package sonata.test.integration;


import static org.junit.Assert.*;

import java.util.Properties;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.control.request.Request;


/**
 * This test validates the basic behaviours of Sonata, such as automatic connection 
 * between Symphony Object instances, configuration, the execution of Translation code
 * etc.
 * 
 * @author godetg
 *
 */
public class BasicSonataOperations {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void configureConnection() {
		fail("Not yet implemented!") ;
	}
	
	@Test
	public void shouldCaptureCreateEntityCalls(){
		fail("Not yet implemented!") ;
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
		super(source, proxy) ;
	}
	
	public void translateCall() {
		AbstractEntityFactory.instance.createEntity(SampleObject2.class) ;
	}
}

///////////////////////////////////////////////////////////////

