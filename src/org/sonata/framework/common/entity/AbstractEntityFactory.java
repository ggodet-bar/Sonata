package org.sonata.framework.common.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonata.framework.common.AbstractFactory;
import org.sonata.framework.common.entity.EntityObjectServices;
import org.sonata.framework.control.invoker.Invoker;

public class AbstractEntityFactory extends AbstractFactory {
	
	private static AbstractEntityFactory instance;
	private Map<Class<?>, Properties> properties ;
//	private Map<Class<?>, Class<EntityObject>> soStructureMapping ;
	private ClassLoader	classLoader ;
	private PropertyInjector injector ;
	
	/**
	 * Map of all Symphony Object instances for each Symphony Object type
	 */
	private final Map<Class<?>,List<EntityObject>> instances_m ;
	
	
	protected AbstractEntityFactory() {
		instances_m = new HashMap<Class<?>, List<EntityObject>>() ;
//		soStructureMapping = new HashMap<Class<?>, Class<EntityObject>>() ;
		properties = new HashMap<Class<?>, Properties>() ;
		classLoader = Thread.currentThread().getContextClassLoader() ;
		injector = new PropertyInjector() ;
	}
	
	public synchronized static AbstractEntityFactory getInstance() {
		if (instance == null) {
			instance = new AbstractEntityFactory() ;
		}
		return instance ;
	}
	
	
	// TODO Au besoin rajouter une description de la structure de l'objet Symphony
	// (quelle noms de classe)
	public boolean register(final Class<?> klazz, final Properties prop) {
		properties.put(klazz, prop) ;
		instances_m.put(klazz, new ArrayList<EntityObject>()) ;
		return true ;
	}
	
	/**
	 * Adds the <code>object</code> element to the list of instances of the 
	 * corresponding class <code>klazz</code>.
	 * @param object the element which is registered in the factory
	 * @return the unique identifier of the object, -1 if the method call
	 * generated an error.
	 */
	public int add(final Class<?> klazz, final EntityObject object) {
		int returnValue = -1 ;
		
		List<EntityObject> list = instances_m.get(klazz) ;
		List<EntityObject> newList = new ArrayList<EntityObject>(list) ;
		newList.add(object) ;
		
		returnValue = newList.indexOf(object) ;
		instances_m.put(klazz, Collections.unmodifiableList(newList)) ;
		
		((EntityObjectServices)object).setID(returnValue) ;

		return returnValue ;
	}
	

	/**
	 * Safe deletes the object from the Symphony Object class <code>klazz</code>
	 * @return true if the deletion was successful, false otherwise
	 */
	public boolean delete(Class<?> klazz, final int identifier) {
		boolean resultValue = false ;

		EntityObject tmp = search(klazz, identifier) ;
		if (tmp != null) {
			List<EntityObject> theInstances = new ArrayList<EntityObject>(instances_m.get(klazz)) ;
			resultValue = theInstances.remove(tmp) ;
			instances_m.put(klazz, Collections.unmodifiableList(theInstances)) ;
		}
		return resultValue ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#listeInstances()
	 */
	public List<EntityObject> instances(Class<?> klazz) {
		return instances_m.get(klazz) ;
	}
	
	/* (non-Javadoc)
	 * @see org.sonata.framework.common.entity.test#rechercher(int)
	 */
	public EntityObject search(Class<?> klazz, final int identifier) {
		EntityObject returnValue = null;
		if (identifier <= instances_m.get(klazz).size()) {
			returnValue = instances_m.get(klazz).get(identifier);
		}
		return returnValue ;
	}

	public Object createEntity(Class<?> klazz) {
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
				anInstance = injector.inject(klazz, anInstance, prop) ;
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
		
		add(klazz, anInstance) ;
		Invoker.getInstance().register(anInstance) ;
		return anInstance;
	}
	
}
