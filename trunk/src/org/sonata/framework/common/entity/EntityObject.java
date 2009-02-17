package org.sonata.framework.common.entity;

import org.sonata.framework.common.SymphonyObject;

/**
 * Interface de marquage pour les Objets Entit�. 
 * @author Guillaume Godet-Bar
 *
 */
public interface EntityObject extends SymphonyObject {

	/**
	 * Renvoie l'identifiant de l'Objet Entit�.
	 * @return l'identifiant
	 */
//	int getID() ;

	/**
	 * D�finit l'identifiant de l'Objet Entit�. Cet identifiant
	 * est unique <b>pour sa classe d'Objet Symphony</b>.
	 * <br />
	 * <b>NB :</b> Cette m�thode doit �tre exclusivement utilis�e
	 * par une Factory !
	 * @param identifier la valeur de l'identifiant
	 */
//	void setID(int identifier);
}
