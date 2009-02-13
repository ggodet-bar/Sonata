package org.sonata.framework.common.interactional;

import java.awt.Dimension;
import java.awt.Point;

import org.sonata.framework.common.TechnicalLayer;

/**
 * Cette interface regroupe les primitives de gestion de l'affichage des Objets Interactionnels.
 * 
 * @author godetg
 *
 */
public interface Displayable {
	
	/**
	 * Définit la position de l'objet dans le repère courant
	 * @param point la position de l'objet
	 */
	void definirPosition(Point point);
	
	/**
	 * Renvoie la position de l'objet dans le repère courant
	 * @return la position de l'objet
	 */
	Point obtenirPosition() ;
	
	/**
	 * Définit la taille de l'objet, par rapport au référentiel courant
	 * @param taille la dimension de l'objet
	 */
	void definirTaille(Dimension taille);
	
	/**
	 * Renvoie la dimension de l'objet, par rapport au référentiel courant
	 * @return la dimension de l'objet
	 */
	Dimension obtenirTaille() ;
	
	/**
	 * Définit l'orientation de l'objet en radians, par rapport au référentiel courant
	 * @param orientation l'orientation de l'objet en radians
	 */
	void definirOrientation(float orientation) ;
	
	/**
	 * Renvoie l'orientation de l'objet en radians, par rapport au référentiel courant
	 * @return l'orientation de l'objet, en radians
	 */
	float obtenirOrientation() ;
	
	/**
	 * Affiche l'objet dans le repère (ou la couche, au sens layer) courant
	 *
	 */
    void afficher() ;
    
    /**
     * Cache l'objet
     *
     */
    void cacher() ;
    
    /**
     * Renvoie vrai si l'objet est visible (i.e., s'il est affiché), faux sinon
     * @return la valeur booléenne
     */
    boolean isVisible() ;
    
    /**
     * Définit la transparence de l'objet, de <code>0f</code> (l'objet est totalement opaque)
     * à <code>1f</code> (l'objet est complètement transparent, donc invisible).<br />
     * <b>NB :</b> un objet invisible n'est pas pour autant caché !
     * @param transparence la transparence de l'objet
     */
    void definirTransparence(final float transparence) ;
    
    /**
     * Renvoie la transparence de l'objet
     * @return la valeur de transparence, de <code>0f</code> à <code>1f</code>
     */
    float obtenirTransparence() ;
    
    /**
     * Renvoie la représentation de l'Objet Interactionnel. À noter que cette représentation
     * est généralement une vue issue de la couche technique de l'Objet Interactionnel. Elle 
     * a typiquement pour vocation d'être rattachée à l'un des layers de visualisation de 
     * la scène, par l'entremise de l'Objet Interactionnel Processus. Il n'est donc pas 
     * conseillé de réaliser une quelconque opération sur la valeur renvoyée, dont le 
     * type dépend de la technologie employée et qui ne peut donc être garanti.
     * @return la représentation technique de l'Objet Interactionnel
     */
    Object obtenirRepresentation() ;
    
    /**
     * Définit la classe chargée de la représentation graphique de l'Objet Interactionnel.
     * <br />
     * <b>NB : </b>Cette méthode ne devrait être utilisée que par la Factory responsable de 
     * la création de l'Objet Interactionnel.
     * @param technicalLayer la classe technique de représentation
     */
    void setTechnicalLayer(final TechnicalLayer technicalLayer) ;

}
