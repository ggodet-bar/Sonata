package org.sonata.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.control.invoker.Invoker;

/*
 * TODO Use the same ClassLoader (singleton within the class)
 */
public class AbstractInitializer {
	
	private InitializerDAO dao ;
	private Properties properties ;
	private Map<Class<?>, Properties> objectProperties_m;
	private List<Class<?>> soClasses ;
	private List<Class<? extends TechnicalComponent>> technicalComponentClasses ;
	
	public AbstractInitializer(InitializerDAO aDAO) {
		objectProperties_m = new HashMap<Class<?>, Properties>() ;
		dao = aDAO ;
		soClasses = new ArrayList<Class<?>>() ;
		technicalComponentClasses = new ArrayList<Class<? extends TechnicalComponent>>() ;
	}
	
	public void loadSymphonyObjects() throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		for (String soName : dao.getSymphonyObjectNames()) {
			soClasses.add(classLoader.loadClass(soName));
		}
	}

	public void loadProperties() throws ClassNotFoundException {
		properties = dao.getProperties() ;
		
		// The properties should then be parsed per referenced SO 
		for (Object aKey : properties.keySet()) {
			String aKeyString = (String)aKey ;
			String objectName = aKeyString.split("\\.")[0] ;
			Class<?> objectClass = getClass(objectName) ;
			if (objectClass == null) {
				throw new ClassNotFoundException() ;
			}
			String objectPropertyName = aKeyString.split("\\.")[1] ;
			
			Properties objectProperties ;
			if (objectProperties_m.containsKey(objectClass)) {
				objectProperties = objectProperties_m.get(objectClass) ;
			} else {
				objectProperties = new Properties() ;
				objectProperties_m.put(objectClass, objectProperties) ;
			}
			objectProperties.setProperty(objectPropertyName, properties.getProperty(aKeyString)) ;
		}
	}
	
	public void loadTechnicalComponents() throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		for (String aComponentName : dao.getTechnicalComponents()) {
			technicalComponentClasses.add((Class<? extends TechnicalComponent>) classLoader.loadClass(aComponentName)) ;
		}
	}
	
	public Properties getProperties(String objectName) {
		return objectProperties_m.get(getClass(objectName)) ;
	}
	
	private Class<?> getClass(String theClassName) {
		for (Class<?> aClass : soClasses) {
			String[] classParts = aClass.getName().split("\\.") ;
			String aClassName = classParts[classParts.length -1] ;
			if (aClassName.equals(theClassName)) {
				return aClass ;
			}
		}
		return null ;
	}
	
	public List<Class<? extends TechnicalComponent>> getTechnicalComponentClasses() {
		return technicalComponentClasses ;
	}
	
	public void setIsUnitTesting(boolean b) {
		Invoker.getInstance().setUnitTesting(b);
		
	}

	public void setupFactory() {
		Map<String, List<String>> component_m = dao.getTechnicalConnections() ;
		for (Class<?> aClass : soClasses) {
			List<Class<? extends TechnicalComponent>> tmpTechComponents = null ;
			// Check if the current class uses a technical component
			if (component_m.containsKey(aClass.getName())) {
				tmpTechComponents = new ArrayList<Class<? extends TechnicalComponent>>() ;
				
				for (String aTechCompName : component_m.get(aClass.getName())) {
					// Verify (assume?) that the technical class is already loaded
					
					for (Class<? extends TechnicalComponent> aTechClass : technicalComponentClasses) {
						if (aTechClass.getName().equals(aTechCompName)) {
							tmpTechComponents.add(aTechClass) ;
							break ;
						}
					}
					
					
					
				}
			}
			
			AbstractEntityFactory.getInstance().register(aClass, objectProperties_m.get(aClass), tmpTechComponents) ;
		}
	}




}
