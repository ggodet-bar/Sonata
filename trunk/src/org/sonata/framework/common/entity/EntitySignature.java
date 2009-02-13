package org.sonata.framework.common.entity;

import java.util.List;

/**
 * Permet de d�finir :
 * <ul>
 * 		<li>	les noms des m�thodes (services) fournis par l'OM	(List[String])</li>
 * 		<li>	les types des arguments associ�s	(List[List[Class]])</li>
 * 		<li>	la classe responsable de la cr�ation	(String)</li>
 * 		<li>	la m�thode de cr�ation (jamais de param�tre)	(String)</li>
 * </ul>
 * La signature est utilis�e par l'<code>Invoker</code> pour l'enregistrement
 * des Objets Entit�
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
