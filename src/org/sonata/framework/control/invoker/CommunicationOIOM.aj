package org.sonata.framework.control.invoker;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.AbstractProcessFactory;

privileged public aspect CommunicationOIOM {
	
	@SuppressAjWarnings({"adviceDidNotMatch"})
	after(AbstractEntityFactory factory) returning (Object e): execution(public Object AbstractEntityFactory.creerEntite()) && target(factory){
		factory.add((EntityObject)e) ;
		Invoker.instance.register((SymphonyObject)e, factory);
	}
	
	@SuppressAjWarnings({"adviceDidNotMatch"})
	after(AbstractProcessFactory factory) returning (Object e): execution(public Object AbstractProcessFactory.creerProcessus()) && target(factory) {
		Invoker.instance.register((SymphonyObject)e, factory) ;
	}	
	
}
