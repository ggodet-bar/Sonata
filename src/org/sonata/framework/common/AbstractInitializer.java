package org.sonata.framework.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.sonata.framework.control.invoker.Invoker;

// Cette classe implemente le pattern singleton. Sa tache consiste a recuperer les proprietes relatives
// aux classes techniques, dans le fichier PropertiesXML.xml.
// Pour chaque objet Symphony mentionne dans le fichier, la factory correspondante doit etre appelee
// et le nom complet de la classe technique doit etre entre en tant que classe de controle, a l'aide 
// de l'appel Class.forName(String)
public abstract class AbstractInitializer {
	
	private static final Object lock = new Object() ;
	
	protected transient Element root ;

	protected transient SAXBuilder sxb = null;

	
	protected static final String TECHNICAL_CLASSES = "technicalLayerClasses" ;
	protected static final String PARAMETERS = "generalParameters" ;
	
	public static AbstractInitializer instance ;

	
	protected AbstractInitializer() {
		synchronized(lock) {
			if (instance == null) {
				instance = this ;
				sxb = new SAXBuilder() ;
			}
		}

	}
	
	public void loadPropertiesFile(final String pathToProperties) {
		try {
			File file = new File(pathToProperties);
			Document doc = sxb.build(file.getAbsoluteFile());
			root = doc.getRootElement() ;
		} catch (JDOMException e) {
			Logger.getAnonymousLogger().severe("Ereur de chargement du fichier de propriétés.\n" + e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().severe("Ereur de chargement du fichier de propriétés.\n" + e.getMessage());
		}
}
	
//	private static String capitalize(String s) {
//        if (s.length() == 0) return s;
//        return s.substring(0, 1).toUpperCase() + s.substring(1);		
//	}
	
	/**
	 * Does all the initialization of technical classes
	 */
	public void setupTechnicalClasses() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader() ;
		Element technicalTreeRoot = root.getChild(TECHNICAL_CLASSES) ;
//		Map<String, > singletonClasses = new ArrayList<String>() ;
		if (technicalTreeRoot != null) {
//			for (Element singleton : (List<Element>)technicalTreeRoot.getChildren("declareSingleton")) {
//				singletonClasses.add(singleton.getValue()) ;
//			}
			
			for (Element symphonyObject : (List<Element>)technicalTreeRoot.getChildren("SymphonyObject")) {
				// Recuperation de l'instance de chaque factory
				String objectName = symphonyObject.getChildText("objectName") ;
				String[] splitName = objectName.split("\\.") ;
				String factoryName = splitName[splitName.length - 1] + "Factory" ;
				String objectPath = "" ;
				for (int i = 0 ; i < splitName.length-1 ; i++) {
					objectPath += splitName[i] + "." ;	// We get the path, except for the last part which needs to be reworked
				}
				objectPath += splitName[splitName.length - 1].toLowerCase() ;
				
				String factoryPath = objectPath + "." + factoryName ;
				try {
					Class<AbstractFactory> factoryClass = (Class<AbstractFactory>) (loader.loadClass(factoryPath)) ;
					Field instanceField = factoryClass.getDeclaredField("instance") ;
					AbstractFactory factory = (AbstractFactory) instanceField.get(instanceField) ;
					String graphicsName = symphonyObject.getChildText("graphicsClassName") ;
					if (graphicsName != null) {
						factory.setGraphics(loader.loadClass(graphicsName));
					}
					String persistenceName = symphonyObject.getChildText("persistenceClassName") ;
					if (persistenceName != null) {
						factory.setPersistence(loader.loadClass(persistenceName));
					}
				} catch (IllegalAccessException e) {
					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
				} catch (ClassNotFoundException e) {
					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
				} catch (SecurityException e) {
					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
				} catch (NoSuchFieldException e) {
					Logger.getAnonymousLogger().severe("Ereur d'initiatlisation.\n" + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Sets all the properties of Symphony Objects (i.e., through their Factory)
	 *
	 */
	public abstract void setupSOParameters() ;
	
	
	public void setIsUnitTesting(boolean b) {
		Invoker.getInstance().setUnitTesting(b);
		
	}
}
