package org.sonata.framework.common;

import java.util.List;

import org.sonata.framework.common.entity.EntityObject;

public aspect SymphonyRoleProtocol {

	declare parents: (SymphonyRole+ && !SymphonyRole) implements SymphonyRoleServices ;
	
	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private EntityObject SymphonyRoleServices.delegate = null;

	@SuppressWarnings("unused")	// Apparently an AspectJ bug
	private static List<EntityObject> delegates;

	public EntityObject SymphonyRoleServices.getTargetSObject() {
		return delegate;
	}

	public void SymphonyRoleServices.setTargetSObject(EntityObject obj) {
		delegate = obj;
	}

	public EntityObject SymphonyRoleServices.search(int objectID) {
		// TODO Should be implemented!!
		return null ;
	}
}
