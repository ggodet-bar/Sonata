package org.sonata.framework.common;

import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.AbstractProcessFactory;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.EntityObjectProtocol;

public privileged aspect TechnicalComponentWeaver {

	private Class<?> currentClass ;
	
	declare precedence: EntityObjectProtocol, SymphonyRoleProtocol, TechnicalComponentWeaver, * ;
	
	before(Class<?> targetClass): execution(Object Abstract*Factory.create*(Class<?>)) && args(targetClass) {
		currentClass = targetClass ;
	}
	
	Object around(SymphonyObject instance) : execution(SymphonyObject+.new()) && target(instance){
		TechnicalComponentLoader loader = null ;
		if (instance instanceof ProcessObject) {
			loader = AbstractProcessFactory.getInstance().techCompLoader ;
		} else if (instance instanceof EntityObject){
			loader = AbstractEntityFactory.getInstance().techCompLoader ;
		}

		
		if (loader.getTechnicalInterfacesForSO(currentClass) != null && 
				!loader.getTechnicalInterfacesForSO(currentClass).isEmpty()) {
			
			try {
				instance = loader.setupTechnicalComponents(currentClass, instance) ;
			} catch (InstantiationException e) {
				e.printStackTrace();
				System.exit(0) ;
			} catch (IllegalAccessException e) {
				System.exit(0) ;
				e.printStackTrace();
			}
		}
		return proceed(instance) ;
	}
}
