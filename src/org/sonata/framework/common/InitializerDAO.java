package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class InitializerDAO {

	protected List<String> soNames ;
	protected Properties theProperties ;
	protected List<String> technicalComponents ;
	protected Map<String, List<String>> technicalConnections ;
	
	public InitializerDAO() {
		theProperties = new Properties() ;
		soNames = new ArrayList<String>() ;
		technicalComponents = new ArrayList<String>() ;
		technicalConnections = new HashMap<String, List<String>>() ;
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
}
