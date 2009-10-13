package org.sonata.framework.common.process;

import java.util.HashMap;
import java.util.Map;

import org.sonata.framework.common.TechnicalComponent;

/**
 * @author Guillaume Godet-Bar
 *
 */
public aspect ProcessObjectProtocol {
	
	declare parents: (ProcessObject+ && !ProcessObject) implements ProcessObjectServices ;
	
	@SuppressWarnings("unused") // Apparently an AspectJ bug
	private Map<Class<? extends TechnicalComponent>, TechnicalComponent> 
	ProcessObjectServices.technicalComponents 
							= new HashMap<Class<? extends TechnicalComponent>, TechnicalComponent>();
	
	public void ProcessObjectServices.setTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz, TechnicalComponent comp) {
		this.technicalComponents.put(klazz, comp) ;
	}
	
	public TechnicalComponent ProcessObjectServices.getTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz) {
		return this.technicalComponents.get(klazz) ;
	}

}
