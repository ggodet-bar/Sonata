package org.sonata.framework.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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

	
	/**
	 * Does all the initialization of technical classes
	 */
//	public void setupTechnicalClasses() {
//		ClassLoader loader = Thread.currentThread().getContextClassLoader() ;
//		Element technicalTreeRoot = root.getChild(TECHNICAL_CLASSES) ;
////		Map<String, > singletonClasses = new ArrayList<String>() ;
//		if (technicalTreeRoot != null) {
////			for (Element singleton : (List<Element>)technicalTreeRoot.getChildren("declareSingleton")) {
////				singletonClasses.add(singleton.getValue()) ;
////			}
//			
//			for (Element symphonyObject : (List<Element>)technicalTreeRoot.getChildren("SymphonyObject")) {
//				// Recuperation de l'instance de chaque factory
//				String objectName = symphonyObject.getChildText("objectName") ;
//				String[] splitName = objectName.split("\\.") ;
//				String factoryName = splitName[splitName.length - 1] + "Factory" ;
//				String objectPath = "" ;
//				for (int i = 0 ; i < splitName.length-1 ; i++) {
//					objectPath += splitName[i] + "." ;	// We get the path, except for the last part which needs to be reworked
//				}
//				objectPath += splitName[splitName.length - 1].toLowerCase() ;
//				
//				String factoryPath = objectPath + "." + factoryName ;
//				try {
//					Class<AbstractFactory> factoryClass = (Class<AbstractFactory>) (loader.loadClass(factoryPath)) ;
//					Field instanceField = factoryClass.getDeclaredField("instance") ;
//					AbstractFactory factory = (AbstractFactory) instanceField.get(instanceField) ;
//					String graphicsName = symphonyObject.getChildText("graphicsClassName") ;
//					if (graphicsName != null) {
//						factory.setGraphics(loader.loadClass(graphicsName));
//					}
//					String persistenceName = symphonyObject.getChildText("persistenceClassName") ;
//					if (persistenceName != null) {
//						factory.setPersistence(loader.loadClass(persistenceName));
//					}
//				} catch (IllegalAccessException e) {
//					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
//				} catch (ClassNotFoundException e) {
//					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
//				} catch (SecurityException e) {
//					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
//				} catch (NoSuchFieldException e) {
//					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
//				}
//			}
//		}
//	}
	
	public void setIsUnitTesting(boolean b) {
		Invoker.getInstance().setUnitTesting(b);
		
	}
}
