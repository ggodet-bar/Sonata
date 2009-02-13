package org.sonata.framework.common.entity;

import java.util.List;

/**
 * Permet de définir :
 * <ul>
 * 		<li>	les noms des méthodes (services) fournis par l'OM	(List[String])</li>
 * 		<li>	les types des arguments associés	(List[List[Class]])</li>
 * 		<li>	la classe responsable de la création	(String)</li>
 * 		<li>	la méthode de création (jamais de paramètre)	(String)</li>
 * </ul>
 * La signature est utilisée par l'<code>Invoker</code> pour l'enregistrement
 * des Objets Entité
 * 
 * @see control.invoker.Invoker Invoker
 * @author Guillaume Godet-Bar
 *
 */
public class EntitySignature {

	public List<String>	listeNomMethodes ;
	public List<List<Class<?>>>	listeTypeParam ;
	public String	classeFactory ;
	public String	methodeCreate ;

}
