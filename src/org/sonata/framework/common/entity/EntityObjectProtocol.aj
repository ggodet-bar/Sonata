package org.sonata.framework.common.entity;

import java.util.HashMap;
import java.util.Map;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.common.entity.EntityObject;

/**
 * Aspect woven into every EntityObject for providing basic identification
 * services. The mechanism attemps to mimick a mixin construct, as used
 * in other programming languages such as C++ or Ruby.
 * 
 * @author Guillaume Godet-Bar
 *
 */
public aspect EntityObjectProtocol {
	
	declare parents: (EntityObject+ && !EntityObject) implements EntityObjectServices ;

	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private int EntityObjectServices.identifier = -1 ;
	
	@SuppressWarnings("unused") // Apparently an AspectJ bug
	private Map<Class<? extends TechnicalComponent>, TechnicalComponent> 
							EntityObjectServices.technicalComponents 
							= new HashMap<Class<? extends TechnicalComponent>, TechnicalComponent>();
	
	public void EntityObjectServices.setID(int id) {
		// Set the identifier strictly once
		if (this.identifier == -1) {
			this.identifier = id ;
		}
	}
	
	public int EntityObjectServices.getID() {
		return this.identifier ;
	}
	
	public int EntityObject.compareTo(EntityObject obj) {
		return ((EntityObjectServices)this).identifier - ((EntityObjectServices)obj).getID() ;
	}
	
	public void EntityObjectServices.setTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz, TechnicalComponent comp) {
		this.technicalComponents.put(klazz, comp) ;
	}
	
	public TechnicalComponent EntityObjectServices.getTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz) {
		return this.technicalComponents.get(klazz) ;
	}

}
