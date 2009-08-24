package sonata.test.unit;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import sonata.test.integration.BasicSonataOperations;
import sonata.test.unit.abstractentityfactory.AbstractEntityFactoryTest;
import sonata.test.unit.invoker.InvokerTest;
import sonata.test.unit.invokerdao.XMLInvokerDAOTest;
import sonata.test.unit.loader.AbstractInitializerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		AbstractEntityFactoryTest.class,
		InvokerTest.class,
		XMLInvokerDAOTest.class
//		AbstractInitializerTest.class,
//		BasicSonataOperations.class
		})
		
public class SonataTestSuite {}
