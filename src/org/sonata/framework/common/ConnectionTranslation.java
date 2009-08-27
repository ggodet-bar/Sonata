package org.sonata.framework.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sonata.framework.common.process.ProcessObject;

/**
 * 
 * @author Guillaume Godet-Bar
 *
 */
public abstract class ConnectionTranslation {

	// Map de toutes les connections g�r�es par chaque classe translation.
	// La map exacte est :
	//		Objet Source	>	[ClasseDestination, InstaceDestination] (destinations)
	private static Map<SymphonyObject, Map<Class<?>, SymphonyObject>> connections_m = new HashMap<SymphonyObject, Map<Class<?>, SymphonyObject>>();
	
	private final SymphonyObject source ;
	private final Map<Class<?>, SymphonyObject> destinations  ;
	private final ProcessObject proxy ;
	
	public static Map<Class<?>, SymphonyObject>getCorrespondance(final SymphonyObject source) {
		return connections_m.get(source) ;
	}
	
	// On suppose que la translation a � sa charge la t�che de remplir la liste des destinations
	public ConnectionTranslation(final SymphonyObject source, final ProcessObject proxy) {
		this.source = source ;
		destinations = new HashMap<Class<?>, SymphonyObject>() ;
		this.proxy = proxy ;
		connections_m.put(source, destinations) ;
	}
	
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this)) return false ;
		ConnectionTranslation test = (ConnectionTranslation) o ;
		Collection<SymphonyObject> destValues = test.destinations.values() ;
		Collection<SymphonyObject> thisValues = destinations.values() ;
		return (test.source.equals(source) &&
				destValues.containsAll(thisValues) &&
				thisValues.containsAll(destValues) &&
				(proxy == null || test.proxy.equals(proxy))
				) ;
	}
	
	public int hashCode() {
		int hash = 13 ;
		hash = hash * 31 + source.getClass().getName().hashCode() ;
		hash = hash * 31 + destinations.size() ;
		return hash ;
	}
	
	public void addDestination(final Class<?> referenceClass, final SymphonyObject destination) {
		destinations.put(referenceClass, destination) ;
	}
	
	public SymphonyObject getDestination(final Class<?> clazz) {
		return destinations.get(clazz) ;
	}

	public SymphonyObject getSource() {
		return source ;
	}
	
	public ProcessObject getProxy() {
		return proxy ;
	}
}
