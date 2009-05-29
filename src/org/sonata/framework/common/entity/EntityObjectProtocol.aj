package org.sonata.framework.common.entity;

public aspect EntityObjectProtocol {
	
	declare parents: (EntityObject+ && !EntityObject) implements EntityObjectServices ;

	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private int EntityObjectServices.identifier = -1 ;
	@SuppressWarnings("unused") // Apparently an AspectJ bug
	private AbstractEntityFactory EntityObjectServices.factory = null ;
	
	public void EntityObjectServices.setID(int id) {
		this.identifier = id ;
	}
	
	public int EntityObjectServices.getID() {
		return identifier ;
	}
	
	public void EntityObjectServices.setFactory(AbstractEntityFactory factory) {
		if (this.factory == null) {	// Should be executed only once (i.e., by the AbstractEntityFactory)
			this.factory = factory ;
		}
	}
	
	public AbstractEntityFactory EntityObjectServices.getFactory() {
		return this.factory ;
	}
}
