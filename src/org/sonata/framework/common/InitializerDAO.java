package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class InitializerDAO {

	protected List<String> soNames ;
	protected Properties theProperties ;
	
	public InitializerDAO() {
		theProperties = new Properties() ;
		soNames = new ArrayList<String>() ;
	}
	
	public Properties getProperties() {
		return theProperties ;
	}

	public List<String> getSymphonyObjectNames() {
		return soNames ;
	}
}
