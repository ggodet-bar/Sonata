package org.sonata.framework.common.entity;

import org.sonata.framework.common.TechnicalComponent;

public interface EntityObjectServices extends Comparable<EntityObject> {
	public void setID(int id) ;
	public int getID() ;
	public void setTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz, TechnicalComponent comp) ;
	public TechnicalComponent getTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz) ;
}
