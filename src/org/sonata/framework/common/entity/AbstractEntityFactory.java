package org.sonata.framework.common.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.control.exceptions.IllegalSymphonyComponent;
import org.sonata.framework.control.invoker.Invoker;

public class AbstractEntityFactory {
	
	private static AbstractEntityFactory instance;
	private static int counter = 1 ;
	private Map<Class<?>, Properties> properties ;
	private ClassLoader	classLoader ;
	private PropertyInjector injector ;
	private TechnicalComponentLoader techCompLoader ;
	
	/**
	 * Map of all Symphony Object instances for each Symphony Object type
	 */
	private final Map<Class<?>,Map<Integer, EntityObject>> instances_m ;
	
	
	protected AbstractEntityFactory() {
		instances_m = new HashMap<Class<?>, Map<Integer, EntityObject>>() ;
		properties = new HashMap<Class<?>, Properties>() ;
		classLoader = Thread.currentThread().getContextClassLoader() ;
		injector = new PropertyInjector() ;
		techCompLoader = new TechnicalComponentLoader() ;
	}
	
	/**
	 * Returns the unique instance of the <code>AbstractEntityFactory</code>
	 * @return an instance of <code>AbstractEntityFactory</code>
	 */
	public synchronized static AbstractEntityFactory getInstance() {
		if (instance == null) {
			instance = new AbstractEntityFactory() ;
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
		instances_m.put(klazz, new TreeMap<Integer, EntityObject>()) ;
		
		try {
			techCompLoader.registerTechnicalInterfaces(klazz, techProp) ;
		} catch (IllegalSymphonyComponent e) {
			e.printStackTrace();
			return false ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false ;
		}
		return true ;
	}
	
	/**
	 * Adds the <code>object</code> element to the list of instances of the 
	 * corresponding class <code>klazz</code>.
	 * @param object the element which is registered in the factory
	 */
	public void add(final Class<?> klazz, final EntityObject object) {
		Map<Integer, EntityObject> theInstances = instances_m.get(klazz) ;
		theInstances.put(((EntityObjectServices)object).getID(), object) ;
	}
	

	/**
	 * Safe deletes the object from the Symphony Object class <code>klazz</code>
	 * @return true if the deletion was successful, false otherwise
	 */
	public boolean delete(Class<?> klazz, final int identifier) {
		Map<Integer, EntityObject> theInstances = instances_m.get(klazz) ;
		if (!theInstances.containsKey(identifier)) return false ;
		theInstances.remove(identifier) ;
		return true ;
	}
	

	/**
	 * Returns the list of Symphony Object instances that match with the interface
	 * <code>klazz</code>
	 * @param klazz
	 * @return the list of instances
	 */
	public List<EntityObject> instances(Class<?> klazz) {
		return new ArrayList<EntityObject>(instances_m.get(klazz).values()) ;
	}
	

	public EntityObject search(Class<?> klazz, final int identifier) {
		Map<Integer, EntityObject> theInstancesList = instances_m.get(klazz) ;
		return theInstancesList.get(identifier) ;
	}

	public synchronized Object createEntity(Class<?> klazz) {
		// If the factory has registered the klazz, then it should
		// be able to return an instance of this SymphonyObject
		EntityObject anInstance = null ;
		if (instances_m != null && klazz.isInterface()) {
			// Get the constructor
			Constructor<EntityObject> constructor;
			try {
				constructor = (Constructor<EntityObject>) classLoader.loadClass(klazz.getName() + "Impl").getConstructor();
				Properties prop = properties.get(klazz) ;
				anInstance = constructor.newInstance() ;
				if (prop != null) {
					anInstance = injector.inject(klazz, anInstance, prop) ;
				}
				anInstance = techCompLoader.setupTechnicalComponents(klazz, anInstance) ;

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		((EntityObjectServices)anInstance).setID(computeUniqueID()) ;
		add(klazz, anInstance) ;
		Invoker.getInstance().bind(anInstance) ;
		return anInstance;
	}

	/**
	 * Returns a unique ID. Uniqueness is guaranteed for all object instances within the same
	 * JVM (thread?) instance.
	 * @return the unique ID
	 */
	private int computeUniqueID() {
		return 13 * ++counter + 7 ;
	}
}
