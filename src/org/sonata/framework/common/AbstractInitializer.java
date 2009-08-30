package org.sonata.framework.common;

import java.util.Properties;

import org.sonata.framework.control.invoker.Invoker;


public class AbstractInitializer {
	
	private InitializerDAO dao ;
	private Properties properties ;

	
	public AbstractInitializer(InitializerDAO aDAO) {
		dao = aDAO ;
	}

	public boolean loadProperties() {
		properties = dao.getProperties() ;
		
		// The properties should then be parsed per referenced SO 
		
		return properties != null ;
	}

	
	public void setIsUnitTesting(boolean b) {
		Invoker.getInstance().setUnitTesting(b);
		
	}
}
