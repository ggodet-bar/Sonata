package org.sonata.framework.common;

import java.util.Properties;

public abstract class InitializerDAO {

	protected Properties theProperties ;
	
	public InitializerDAO() {
		theProperties = new Properties() ;
	}
	
	public Properties getProperties() {
		return theProperties ;
	}
}
