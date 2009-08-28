package org.sonata.framework.common.entity;

import org.sonata.framework.common.entity.EntityObject;

/**
 * Aspect woven into every EntityObject for providing basic identification
 * services. The mechanism attemps to mimick a Mixin construct, as used
 * in other programming languages such as C++ or Ruby.
 * 
 * @author Guillaume Godet-Bar
 *
 */
public aspect EntityObjectProtocol {
	
	declare parents: (EntityObject+ && !EntityObject) implements EntityObjectServices ;

	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private int EntityObjectServices.identifier = -1 ;
	
	public void EntityObjectServices.setID(int id) {
		this.identifier = id ;
	}
	
	public int EntityObjectServices.getID() {
		return this.identifier ;
	}
	
	public int EntityObject.compareTo(EntityObject obj) {
		return ((EntityObjectServices)this).identifier - ((EntityObjectServices)obj).getID() ;
	}
}
