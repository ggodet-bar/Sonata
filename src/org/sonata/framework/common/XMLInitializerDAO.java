package org.sonata.framework.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class XMLInitializerDAO extends InitializerDAO {
	
	public void loadPropertiesFromFile(String filename) throws InvalidPropertiesFormatException, IOException {
		File f = new File(filename) ;
		FileInputStream fis = new FileInputStream(f) ; 
		
		Properties loadedProperties = new Properties() ;
		loadedProperties.loadFromXML(fis) ;
		theProperties = loadedProperties ;
	}
}
