package org.sonata.framework.common.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.sonata.framework.common.SymphonyObject;

class PropertyInjector {

	<T extends SymphonyObject> T inject(Class<?> klazz, T instance, Properties prop) throws NoSuchMethodException {
		for (Object aKey : prop.keySet()) {
			List<String> methodNames = toMethodNames((String)aKey) ;
			
			String[] preArgumentArray = parseArguments(prop.getProperty((String)aKey)) ;
			
			Method theMethod = getMatchingMethod(klazz, methodNames, preArgumentArray) ;
			
			if (theMethod == null) throw new NoSuchMethodException("No matching method found for property " + aKey) ;
			
			
			// In order for the injection to work, the parameter classes should have a constructor 
			// with string parameters, except for primitive types, which are treated here
			Object[] argumentArray = castArguments(theMethod.getParameterTypes(), preArgumentArray) ;
			
			try {
				theMethod.invoke(instance, (Object[])argumentArray) ;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new NoSuchMethodException("No matching method found for property " + aKey) ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new NoSuchMethodException("No matching method found for property " + aKey) ;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new NoSuchMethodException("No matching method found for property " + aKey) ;
			}
		}

		return instance ;
	}
	
	private Object[] castArguments(Class<?>[] typesList, String[] preArgs) {
		List<Object> args = new LinkedList<Object>() ;
		
		for (int i = 0 ; i < typesList.length ; i++) {
			Class<?> currentType = typesList[i] ;
			String currentArg = preArgs[i] ;

			if (currentType == int.class) {
				args.add(new Integer(currentArg)) ;
			} else if (currentType == byte.class) {
				args.add(new Byte(currentArg)) ;
			} else if (currentType == short.class) {
				args.add(new Short(currentArg)) ;
			} else if (currentType == long.class) {
				args.add(new Long(currentArg)) ;
			} else if (currentType == boolean.class) {
				args.add(new Boolean(currentArg)) ;
			} else if (currentType == float.class) {
				args.add(new Float(currentArg)) ;
			} else if (currentType == double.class) {
				args.add(new Double(currentArg)) ;
			} else if (currentType == char.class) {
				args.add(new Character(currentArg.charAt(0))) ;
			} else {
				args.add(currentArg) ;
			}
		}
		
		return args.toArray();
	}
	
	private String[] parseArguments(String s) {
		List<String> arguments = new LinkedList<String>() ;
		Scanner scanner = new Scanner(s) ;
		scanner.useDelimiter("\\w") ;
		while(scanner.hasNext()) {
			if (scanner.hasNext("\"")) {	// Then we are parsing a long string containing commas
				scanner.useDelimiter("\"") ;
				arguments.add(scanner.next().trim()) ;
			} else {
				scanner.useDelimiter(",") ;
				arguments.add(scanner.next().trim()) ;
			}
		}
		scanner.close() ;
		
		return arguments.toArray(new String[]{}) ;
	}
	
	/**
	 * Generates all the possible setter names, based on JavaBeans conventions
	 * @param s
	 * @return
	 */
	private List<String> toMethodNames(String s) {
		String baseName =  s.substring(0, 1).toUpperCase() + s.substring(1) ;
		List<String> result = new LinkedList<String>() ;
		result.add("set" + baseName) ;
		return result ;
	}
	
	private Method getMatchingMethod(Class<?> klazz, List<String> methodNames, String... arguments) {
		Method theMethod = null ;
		for (Method aMethod : klazz.getMethods()) {
			if (	methodNames.contains(aMethod.getName())	&&
					aMethod.getParameterTypes().length == arguments.length) {
				theMethod = aMethod ;
				break ;
			}
		}
		
		return theMethod ;
	}
}
