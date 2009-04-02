package org.sonata.framework.common;

import java.util.HashMap;
import java.util.Map;

import org.sonata.framework.common.process.ProcessObject;

public class ConnectionTranslation {

	// Map de toutes les connections gérées par chaque classe translation.
	// La map exacte est :
	//		Objet Source	>	[ClasseDestination, InstaceDestination] (destinations)
	private static Map<SymphonyObject, Map<Class<SymphonyObject>, SymphonyObject>> connections_m = new HashMap<SymphonyObject, Map<Class<SymphonyObject>, SymphonyObject>>();
	
	private final transient SymphonyObject source ;
	private final transient Map<Class<SymphonyObject>, SymphonyObject> destinations  ;
	private final transient ProcessObject proxy ;
	
	public static Map<Class<SymphonyObject>, SymphonyObject>getCorrespondance(final SymphonyObject source) {
		return connections_m.get(source) ;
	}
	
	// On suppose que la translation a à sa charge la tâche de remplir la liste des destinations
	public ConnectionTranslation(final SymphonyObject source, final ProcessObject proxy) {
		this.source = source ;
		destinations = new HashMap<Class<SymphonyObject>, SymphonyObject>() ;
		this.proxy = proxy ;
		connections_m.put(source, destinations) ;
	}
	
	public void addDestination(final Class<SymphonyObject> clazz, final SymphonyObject destination) {
		destinations.put(clazz, destination) ;
	}
	
	public SymphonyObject getDestination(final Class<SymphonyObject> clazz) {
		return destinations.get(clazz) ;
	}
	

	public SymphonyObject getSource() {
		return source ;
	}
	
	public ProcessObject getProxy() {
		return proxy ;
	}
}
