package org.sonata.framework.common;

import org.sonata.framework.common.entity.EntityObject;


public interface SymphonyRoleServices {
	EntityObject getTargetSObject() ;
	void setTargetSObject(EntityObject obj) ;
	EntityObject search(int objectID) ;
	
}
