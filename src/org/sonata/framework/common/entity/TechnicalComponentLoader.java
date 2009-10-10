package org.sonata.framework.common.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.control.exceptions.IllegalSymphonyComponent;

/**
 * 
 * @author Guillaume Godet-Bar
 *
 */
class TechnicalComponentLoader {
	
	/**
	 * The map that associates the Object Symphony interfaces with the list
	 * of interfaces that extends <code>TechnicalComponent</code>.
	 */
	private Map<Class<?>, List<Class<? extends TechnicalComponent>>> technicalInterfaces ;
	private Map<Class<?>, List<Class<? extends TechnicalComponent>>> technicalComponents ;

	public TechnicalComponentLoader () {
		technicalInterfaces = new HashMap<Class<?>, List<Class<? extends TechnicalComponent>>>() ;
		technicalComponents = new HashMap<Class<?>, List<Class<? extends TechnicalComponent>>>() ;
	}
	
	/**
	 * Scans the namespace and sub-namespaces of class <code>klazz</code> and registers 
	 * all the interfaces that extend the type <code>TechnicalComponent</code>.
	 * Throws IllegalSymphonyComponent if the scanning of the namespace fails for some
	 * reason.
	 * @param klazz a Symphony Object interface
	 * @param components
	 * @throws IllegalSymphonyComponent
	 * @throws ClassNotFoundException 
	 */
	public void registerTechnicalInterfaces(Class<?> klazz, List<Class<? extends TechnicalComponent>> components) throws IllegalSymphonyComponent, ClassNotFoundException {	
		technicalInterfaces.put(klazz, listInterfaces(klazz)) ;
		technicalComponents.put(klazz, components) ;
	}
	
	/**
	 * Returns an unmodifiable list of interfaces that extend the type 
	 * <code>TechnicalComponent</code>, for class <code>klazz</code>.
	 * @param klazz a Symphony Object interface
	 * @return the list of interfaces
	 */
	public List<Class<? extends TechnicalComponent>> getTechnicalInterfacesForSO(Class<?> klazz) {
		return Collections.unmodifiableList(technicalInterfaces.get(klazz)) ;
	}

	public EntityObject setupTechnicalComponents(Class<?> klazz, EntityObject instance) throws InstantiationException, IllegalAccessException {
		if (!technicalComponents.containsKey(klazz) || technicalComponents.get(klazz) == null) return instance ;
		for (Class<? extends TechnicalComponent> aComponentInterface : technicalComponents.get(klazz)) {
			// Get the actual component that implements the interface
			TechnicalComponent aComponent = aComponentInterface.newInstance() ;
			
			for (Class<? extends TechnicalComponent> anInterface : technicalInterfaces.get(klazz)) {
				if (Arrays.asList(aComponent.getClass().getInterfaces()).contains(anInterface)) {
					((EntityObjectServices)instance).setTechnicalComponentInstance(anInterface, aComponent) ;
					break ;
				}
			}
		}
		return instance ;
	}
	
	/**
	 * Returns the list of interfaces that extend the type <code>TechnicalComponent</code>,
	 * for class <code>baseClass</code>.
	 * @param baseClass a Symphony Object interface
	 * @return the list of interfaces
	 * @throws IllegalSymphonyComponent
	 */
	private List<Class<? extends TechnicalComponent>> listInterfaces(Class<?> baseClass) throws IllegalSymphonyComponent {
		List<Class<? extends TechnicalComponent>> technicalInterfaces = new ArrayList<Class<? extends TechnicalComponent>>() ;
		
		String namespace = getNamespace(baseClass) ;
		
		File baseDir = getBaseDirectory(namespace) ;
		if (baseDir == null) throw new IllegalSymphonyComponent() ;
		
		List<String> classNames = scanSubDirectories(baseDir, namespace) ;
		for (String aClassName : classNames) {
			try {
				Class<?> theClass = Thread.currentThread().getContextClassLoader().loadClass(aClassName) ;
				if (TechnicalComponent.class.isAssignableFrom(theClass) && theClass.isInterface()) {
					technicalInterfaces.add((Class<? extends TechnicalComponent>) theClass) ;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return technicalInterfaces;
	}
	
	/**
	 * Returns the name of the package (namespace) which contains
	 * the Object Symphony interface.
	 * @param baseClass
	 * @return the name of the package
	 */
	private String getNamespace(Class<?> baseClass) {
		String className = baseClass.getName() ;
		List<String> nameElements = Arrays.asList(className.split("\\.")) ;
		nameElements = nameElements.subList(0, nameElements.size() - 1) ;
		
		StringBuilder sb = new StringBuilder(nameElements.get(0)) ;
		for (String anElement : nameElements.subList(1, nameElements.size())) {
			sb.append(".").append(anElement) ;
		}
		return sb.toString() ;
	}
	
	/*
	 * TODO Scan the jar files!
	 */
	/**
	 * Returns the list of files for each subdirectory (or subpackage) of the
	 * directory <code>baseDir</code>, which corresponds to the package name
	 * <code>namespace</code>.
	 */
	private List<String> scanSubDirectories(File baseDir, String namespace) {
		List<String> result = new LinkedList<String>() ;
		recScanSubDirectories(baseDir, result, namespace) ;
		return result;
	}

	/**
	 * Recursive method which actually realizes the contract of method 
	 * <code>scanSubDirectories</code>.
	 * @param baseDir
	 * @param result an accumulator for the current results
	 * @param currentNamespace
	 */
	private void recScanSubDirectories(File baseDir, List<String> result, String currentNamespace) {
		for (File aFile: baseDir.listFiles()) {
			if (aFile.isDirectory()) {
				recScanSubDirectories(aFile, result, currentNamespace + "." + aFile.getName()) ;
			} else {
				result.add(currentNamespace + "." + aFile.getName().split("\\.")[0]) ;
			}
		}
		
	}

	/**
	 * Returns the File object that corresponds to the filesystem directory
	 * matching the <code>namespace</code> package.
	 * @param namespace
	 * @return the base directory as a File object
	 */
	private File getBaseDirectory(String namespace) {
		
		List<String> pathElements = Arrays.asList(namespace.split("\\.")) ;
		
		// We get the path up to the base class, not included
		StringBuilder dirPath;
		
		String classpath = System.getProperty("java.class.path") ;
		
		// Search the current class within the classpath
		String[] classpathDirectories = classpath.split(File.pathSeparator) ;
		
		for (String aPath : classpathDirectories) {
		
			try {
				dirPath = new StringBuilder(new File(aPath).getCanonicalPath()).append(File.separator);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			for (String anElement : pathElements) {
				dirPath.append(anElement).append(File.separator) ;
			}
			File baseDir = new File(dirPath.toString()) ;
			
			if (baseDir.exists() && baseDir.isDirectory()) {
				return baseDir ;
			}
		}
			return null ;
	}



}
