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
	 * D�finit la position de l'objet dans le rep�re courant
	 * @param point la position de l'objet
	 */
	void definirPosition(Point point);
	
	/**
	 * Renvoie la position de l'objet dans le rep�re courant
	 * @return la position de l'objet
	 */
	Point obtenirPosition() ;
	
	/**
	 * D�finit la taille de l'objet, par rapport au r�f�rentiel courant
	 * @param taille la dimension de l'objet
	 */
	void definirTaille(Dimension taille);
	
	/**
	 * Renvoie la dimension de l'objet, par rapport au r�f�rentiel courant
	 * @return la dimension de l'objet
	 */
	Dimension obtenirTaille() ;
	
	/**
	 * D�finit l'orientation de l'objet en radians, par rapport au r�f�rentiel courant
	 * @param orientation l'orientation de l'objet en radians
	 */
	void definirOrientation(float orientation) ;
	
	/**
	 * Renvoie l'orientation de l'objet en radians, par rapport au r�f�rentiel courant
	 * @return l'orientation de l'objet, en radians
	 */
	float obtenirOrientation() ;
	
	/**
	 * Affiche l'objet dans le rep�re (ou la couche, au sens layer) courant
	 *
	 */
    void afficher() ;
    
    /**
     * Cache l'objet
     *
     */
    void cacher() ;
    
    /**
     * Renvoie vrai si l'objet est visible (i.e., s'il est affich�), faux sinon
     * @return la valeur bool�enne
     */
    boolean isVisible() ;
    
    /**
     * D�finit la transparence de l'objet, de <code>0f</code> (l'objet est totalement opaque)
     * � <code>1f</code> (l'objet est compl�tement transparent, donc invisible).<br />
     * <b>NB :</b> un objet invisible n'est pas pour autant cach� !
     * @param transparence la transparence de l'objet
     */
    void definirTransparence(final float transparence) ;
    
    /**
     * Renvoie la transparence de l'objet
     * @return la valeur de transparence, de <code>0f</code> � <code>1f</code>
     */
    float obtenirTransparence() ;
    
    /**
     * Renvoie la repr�sentation de l'Objet Interactionnel. � noter que cette repr�sentation
     * est g�n�ralement une vue issue de la couche technique de l'Objet Interactionnel. Elle 
     * a typiquement pour vocation d'�tre rattach�e � l'un des layers de visualisation de 
     * la sc�ne, par l'entremise de l'Objet Interactionnel Processus. Il n'est donc pas 
     * conseill� de r�aliser une quelconque op�ration sur la valeur renvoy�e, dont le 
     * type d�pend de la technologie employ�e et qui ne peut donc �tre garanti.
     * @return la repr�sentation technique de l'Objet Interactionnel
     */
    Object obtenirRepresentation() ;
    
    /**
     * D�finit la classe charg�e de la repr�sentation graphique de l'Objet Interactionnel.
     * <br />
     * <b>NB : </b>Cette m�thode ne devrait �tre utilis�e que par la Factory responsable de 
     * la cr�ation de l'Objet Interactionnel.
     * @param technicalLayer la classe technique de repr�sentation
     */
    void setTechnicalLayer(final TechnicalLayer technicalLayer) ;

}
