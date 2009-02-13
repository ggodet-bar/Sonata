package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite de gestion des instances d'Objets Symphony. Tout Objet Symphony devra 
 * décrire une classe Factory étendant <code>AbstractEntityFactory</code>.
 * 
 * @author godetg
 *
 */
public abstract class AbstractFactory {
	
	/**
	 * Instance unique de la factory. Cette dernière doit être redéfinie 
	 * pour toutes les instances de <code>AbstractEntityFactory</code>.
	 */
	public static AbstractFactory instance ;
	
	/**
	 * Participe au mécanisme du singleton (on compare la classe courante à la 
	 * liste des instances)
	 */
	private static List<Class<AbstractFactory>> instantiatedSubclasses = new ArrayList<Class<AbstractFactory>>() ;
	
	/**
	 * Classe technique gérant l'aspect graphique (Objets Interactionnels)
	 */
	private Class<?> graphics ;
	
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
	
	public void setPersistence(final Class<?> persistence) {
		this.persistence = persistence ;
	}
	
	public Class<?> getPersistence() {
		return persistence ;
	}
	
}
