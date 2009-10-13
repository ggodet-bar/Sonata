package sonata.test.unit;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import sample.test.testprocessobjects.ProcessObjectTest;
import sonata.test.integration.BasicSonataOperations;
import sonata.test.unit.abstractentityfactory.AbstractEntityFactoryTest;
import sonata.test.unit.invoker.InvokerTest;
import sonata.test.unit.jsoninvokerdao.JSONInvokerDAOTest;
import sonata.test.unit.loader.AbstractInitializerTest;
import sonata.test.unit.xmlinvokerdao.XMLInvokerDAOTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		AbstractEntityFactoryTest.class,
		InvokerTest.class,
		XMLInvokerDAOTest.class,
		JSONInvokerDAOTest.class,
		AbstractInitializerTest.class,
		BasicSonataOperations.class,
		ProcessObjectTest.class
		})
		
public class SonataTestSuite {}
