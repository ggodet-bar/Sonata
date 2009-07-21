package org.sonata.framework.common.entity;

public aspect EntityObjectProtocol {
	
	declare parents: (EntityObject+ && !EntityObject) implements EntityObjectServices ;

	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private int EntityObjectServices.identifier = -1 ;
	
	public void EntityObjectServices.setID(int id) {
		this.identifier = id ;
	}
	
	public int EntityObjectServices.getID() {
		return identifier ;
	}
}
