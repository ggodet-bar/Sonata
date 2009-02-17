package org.sonata.framework.common.entity;

import java.util.ArrayList;
import java.util.List;

import org.sonata.framework.common.AbstractFactory;
import org.sonata.framework.common.entity.EntityObjectProtocol.EntityObjectServices;

/**
 * Classe abstraite de gestion des instances d'Objets Symphony. Tout Objet Symphony devra 
 * décrire une classe Factory étendant <code>AbstractEntityFactory</code>.
 * 
 * @author godetg
 *
 */
public abstract class AbstractEntityFactory extends AbstractFactory {
	
	/**
	 * Différencie les factories gérant une seule instance de classe
	 * des factories gérant plusieurs instances (typiquement, les factories
	 * gérant les Objets Processus par rapport aux factories gérant la 
	 * plupart des Objets Entité)
	 */
	private boolean isEntitySingleton = false ;
	
	/**
	 * Liste des instances d'Objets Entité du type géré par la Factory
	 */
	private final List<EntityObject> listeInstances ;
	
	
	protected AbstractEntityFactory() {
		super() ;
		listeInstances = new ArrayList<EntityObject>();
	}

	/**
	 * Ajoute l'Objet Entité <code>object</code> à la liste des instances gérées
	 * par l'instance de <code>AbstractEntityFactory</code>. La méthode renvoie
	 * l'identifiant affecté à l'objet.
	 * @param object l'objet à enregistrer auprès de la factory
	 * @return l'identifiant de l'objet, -1 si l'ajout a généré une erreur
	 */
	public int add(final EntityObject object) {
		boolean successfullyAdded = false ;
		int returnValue ;
		if (!(isEntitySingleton && !listeInstances.isEmpty())) {
			successfullyAdded = listeInstances.add(object);
		}
		if (successfullyAdded) {
			returnValue = listeInstances.indexOf(object) ;
			((EntityObjectServices)object).setID(returnValue) ;
			((EntityObjectServices)object).setFactory(this) ;
		} else {
			returnValue = -1 ;
		}
		return returnValue ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#supprimer(int)
	 */
	public void supprimer(final int identifiant) {
		EntityObject temp = rechercher(identifiant) ;
		if (temp != null) {
			listeInstances.remove(temp);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#listeInstances()
	 */
	public List<Object> listeInstances() {
		List<Object> listeClonee = (List<Object>) ((ArrayList<EntityObject>)listeInstances).clone() ;
		return listeClonee ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#rechercher(int)
	 */
	public EntityObject rechercher(final int identifiant) {
		EntityObject returnValue = null;
		if (identifiant <= listeInstances.size()) {
			returnValue = listeInstances.get(identifiant);
		}
		return returnValue ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#setEntitySingleton(boolean)
	 */
	public void setEntitySingleton(final boolean trueFalse) {
		isEntitySingleton = trueFalse ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#isEntitySingleton()
	 */
	public boolean isEntitySingleton() {
		return isEntitySingleton ;
	}
	
	public abstract Object creerEntite() ;
	
}
