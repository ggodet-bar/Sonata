package org.sonata.framework.common.entity;

import org.sonata.framework.common.SymphonyRoleProtocol;

public privileged aspect TechnicalComponentWeaver {

	private Class<?> currentClass ;
	
	declare precedence: EntityObjectProtocol, SymphonyRoleProtocol, TechnicalComponentWeaver, * ;
	
	before(Class<?> targetClass): execution(Object AbstractEntityFactory.createEntity(Class<?>)) && args(targetClass) {
		currentClass = targetClass ;
	}
	
	Object around(EntityObject instance) : execution(EntityObject+.new()) && target(instance){

		TechnicalComponentLoader loader = AbstractEntityFactory.getInstance().techCompLoader ;
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
