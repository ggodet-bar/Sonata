package org.sonata.framework.common.entity;

import org.sonata.framework.common.SymphonyObject;

/**
 * Interface de marquage pour les Objets Entité. 
 * @author Guillaume Godet-Bar
 *
 */
public interface EntityObject extends SymphonyObject {

	/**
	 * Renvoie l'identifiant de l'Objet Entité.
	 * @return l'identifiant
	 */
//	int getID() ;

	/**
	 * Définit l'identifiant de l'Objet Entité. Cet identifiant
	 * est unique <b>pour sa classe d'Objet Symphony</b>.
	 * <br />
	 * <b>NB :</b> Cette méthode doit être exclusivement utilisée
	 * par une Factory !
	 * @param identifier la valeur de l'identifiant
	 */
//	void setID(int identifier);
}
