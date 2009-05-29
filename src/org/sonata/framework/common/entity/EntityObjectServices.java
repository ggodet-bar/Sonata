package org.sonata.framework.common.entity;

public interface EntityObjectServices {
	public void setID(int id) ;
	public int getID() ;
	public AbstractEntityFactory getFactory() ;
	public void setFactory(AbstractEntityFactory factory) ;
}