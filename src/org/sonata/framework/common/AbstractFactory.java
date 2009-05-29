package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for managing Symphony Object instances. Every SO component should 
 * integrate a Factory class that extends <code>AbstractEntityFactory</code>.
 * 
 * @author godetg
 *
 */
public class AbstractFactory {

	/**
	 * Singleton instance of the factory. <b>It should be redefined by every 
	 * instance of <code>AbstractEntityFactory</code>.
	 */
	public static AbstractFactory instance ;
	
	/**
	 * This attribute is part of the singleton mechanism (it stores AbstractFactory
	 * extension classes, which should be unique).
	 */
	private static List<Class<AbstractFactory>> instantiatedSubclasses = new ArrayList<Class<AbstractFactory>>() ;
	
	/**
	 * Reference to the technical implementation for graphics rendering
	 * (Interactional Objects only).
	 */
	private Class<?> graphics ;
	
	/**
	 * Reference to the technical implementation of persistence
	 */
	private Class<?> persistence ;
	
	protected AbstractFactory() {
		synchronized (instantiatedSubclasses) {
			if (instantiatedSubclasses.contains(getClass())) {
				throw new IllegalStateException(
						"Attempt to create more than one instance of class "
								+ getClass().getName()
								+ ". Class should be a singleton");
			}
			instance = this;
			instantiatedSubclasses.add((Class<AbstractFactory>) getClass());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#setGraphics(java.lang.Class)
	 */
	public void setGraphics(final Class<?> graphics) {
		this.graphics = graphics ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#getGraphics()
	 */
	public Class<?> getGraphics() {
		return this.graphics ;
	}
	
	/**
	 * Setter for the read-only <code>persistence</code> attribute.
	 * @param persistence
	 */
	public void setPersistence(final Class<?> persistence) {
		this.persistence = persistence ;
	}
	
	/**
	 * Setter for the read-only <code>graphics</code> attribute.
	 * @return
	 */
	public Class<?> getPersistence() {
		return persistence ;
	}
	
}
