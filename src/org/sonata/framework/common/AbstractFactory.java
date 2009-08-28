package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.List;

public class AbstractFactory {

	/**
	 * Singleton instance of the factory. <b>It should be redefined by every 
	 * instance of <code>AbstractEntityFactory</code></b>.
	 */
	//public static AbstractFactory instance ;
	
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
//		synchronized (instantiatedSubclasses) {
//			if (instantiatedSubclasses.contains(getClass())) {
//				throw new IllegalStateException(
//						"Attempt to create more than one instance of class "
//								+ getClass().getName()
//								+ ". Class should be a singleton");
//			}
////			instance = this;
//			instantiatedSubclasses.add((Class<AbstractFactory>) getClass());
//		}
	}
	
	/**
	 * Setter for the read-only <code>graphics</code> attribute.
	 * @param graphics
	 */
	public void setGraphics(final Class<?> graphics) {
		this.graphics = graphics ;
	}
	
	/**
	 * Getter for the read-only graphics class
	 * @return the graphics class
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
	 * Getter for the read-only persistence class
	 * @return the persistence class
	 */
	public Class<?> getPersistence() {
		return persistence ;
	}
	
}
