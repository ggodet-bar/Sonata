package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonata.framework.control.invoker.BrokerReference;

public abstract class InitializerDAO {

	protected List<String> soNames ;
	protected Properties theProperties ;
	protected List<String> technicalComponents ;
	protected Map<String, List<String>> technicalConnections ;
	protected List<BrokerReference>	theReferences ;
	
	public InitializerDAO() {
		theProperties = new Properties() ;
		soNames = new ArrayList<String>() ;
		technicalComponents = new ArrayList<String>() ;
		technicalConnections = new HashMap<String, List<String>>() ;
		theReferences = new LinkedList<BrokerReference>() ;
	}
	
	public Properties getProperties() {
		return theProperties ;
	}

	public List<String> getSymphonyObjectNames() {
		return soNames ;
	}
	
	public List<String> getTechnicalComponents() {
		return technicalComponents ;
	}
	
	public Map<String, List<String>> getTechnicalConnections() {
		return technicalConnections ;
	}
	
	public List<BrokerReference> getBrokerReferences() {
		return theReferences ;
	}
}
