package org.sonata.framework.common;

import java.util.List ;

import org.sonata.framework.common.entity.EntityObject ;
import org.sonata.framework.common.entity.AbstractEntityFactory ;
import org.sonata.framework.common.entity.EntityObjectServices ;

public aspect SymphonyRoleProtocol {

	declare parents: (SymphonyRole+ && !SymphonyRole) implements SymphonyRoleServices ;
	
	private EntityObject SymphonyRoleServices.delegate = null;

	private static AbstractEntityFactory factory = null;

	private static List<EntityObject> delegates;

	public EntityObject SymphonyRoleServices.getTargetSObject() {
		return delegate;
	}

	public void SymphonyRoleServices.setTargetSObject(EntityObject obj) {
		delegate = obj;
		factory = ((EntityObjectServices) obj).getFactory();
	}

	public EntityObject SymphonyRoleServices.search(int objectID) {
		return null ;
	}
	
	static private void aTest() {
		
	}
}