package sonata.test.integration;


import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.ProcessObject;

import sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2;

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

class SampleObjectTranslation extends ConnectionTranslation {

	public SampleObjectTranslation(SymphonyObject source, ProcessObject proxy) {
		super(source, proxy) ;
	}
	
	public void translateCall() {
		AbstractEntityFactory.getInstance().createEntity(SampleObject2.class) ;
	}
}

///////////////////////////////////////////////////////////////

