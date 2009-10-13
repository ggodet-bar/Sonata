package org.sonata.framework.common.process;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.common.TechnicalComponentLoader;
import org.sonata.framework.control.exceptions.IllegalSymphonyComponent;

public class AbstractProcessFactory {

	private Map<Class<?>, Properties> properties ;
	private static AbstractProcessFactory instance ;
	private Map<Class<?>, ProcessObject> instances ;
	private ClassLoader classLoader ;
	private TechnicalComponentLoader techCompLoader ;
	
	private AbstractProcessFactory() {
		classLoader =  Thread.currentThread().getContextClassLoader() ;
		instances = new HashMap<Class<?>, ProcessObject>() ;
		techCompLoader = new TechnicalComponentLoader() ;
		properties = new HashMap<Class<?>, Properties>() ;
	}
	
	public static AbstractProcessFactory getInstance() {
		if (instance == null) {
			instance = new AbstractProcessFactory() ;
		}
		return instance ;
	}
	
	/**
	 * Registers the Symphony Object defined by the interface <code>klazz</code>
	 * and the list of properties <code>prop</code>.
	 * 
	 * @param klazz the interface of the Symphony Object
	 * @param prop the general properties of the Symphony Object
	 * @param techProp the technical components, mapped to the corresponding interface class
	 * @return <code>true</code> if the registration process succeeded, 
	 * or else <code>false</code> (e.g. if the class <code>klazz</code> does not
	 * designate a valid Symphony Object).
	 */
	public boolean register(final Class<?> klazz, final Properties prop, final List<Class<? extends TechnicalComponent>> techProp) {
		properties.put(klazz, prop) ;
		
		if (techProp != null && !techProp.isEmpty()) {
			try {
				techCompLoader.registerTechnicalInterfaces(klazz, techProp) ;
			} catch (IllegalSymphonyComponent e) {
				e.printStackTrace();
				return false ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false ;
			}
		}
		return true ;
	}
	
	public Object createProcess(Class<?> klazz) {
		if (instances.containsKey(klazz)) {
			return instances.get(klazz) ;
		}
		Constructor<ProcessObject> constructor = null;
		try {
			constructor = (Constructor<ProcessObject>)classLoader.loadClass(klazz.getName() + "Impl").getConstructor();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		ProcessObject anInstance = null ;
		try {
			anInstance = constructor.newInstance();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		instances.put(klazz, anInstance) ;
		
		return anInstance ;
	}
}
