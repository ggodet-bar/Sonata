package org.sonata.framework.common.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.sonata.framework.common.SymphonyObject;

class PropertyInjector {

	/**
	 * 
	 * @param <T>
	 * @param klazz
	 * @param instance
	 * @param prop
	 * @return
	 * @throws NoSuchMethodException
	 */
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
	
	/**
	 * Casts string arguments to the corresponding primitive types, when necessary.
	 * Uses the <code>typesList</code> of the target method in order to infer on
	 * the possible "casts" to primitive types. If the target type is either not
	 * a primitive or a String, then the String argmument from <code>preArgs</code>
	 * is not copied in the resulting array, as is. 
	 * 
	 * @param typesList the array of types that should be cast to
	 * @param preArgs the initial array of string arguments
	 * @return the object array of the cast arguments
	 */
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
	
	/**
	 * Parses the passed string into an array of arguments represented as strings.
	 * Arguments can be passed as comma-separated strings.
	 * Should the argument itself include commas, the argument may be
	 * passed between escaped (\") quotes, e.g.:<br />
	 * <code>"\"I, a long argument with, y'know, commas\", 14, a 
	 * simple string argument"</code><br />
	 * Also, note that all arguments are trimmed for leading and
	 * trailing spaces. The second argument of the above example 
	 * would therefore be parsed as <code>"14"</code> and not
	 * <code>" 14"</code>.
	 * @param s the whole string representing the list of arguments
	 * @return the array of arguments
	 */
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
	 * Generates all the possible setter names for the property named <code>name</code>,
	 * based on JavaBeans conventions, and returns them as a list of strings.
	 * @param name
	 * @return
	 */
	private List<String> toMethodNames(String name) {
		String baseName =  name.substring(0, 1).toUpperCase() + name.substring(1) ;
		List<String> result = new LinkedList<String>() ;
		result.add("set" + baseName) ;
		return result ;
	}
	
	/**
	 * Returns a method corresponding to the passed name(s) and parameters.
	 * 
	 * Checks for any method within class <code>klazz</code> with:
	 * <ul>
	 * 	<li>the same name as any contained in <code>methodNames</code>,</li>
	 *  <li>the same number of parameters as contained in <code>arguments</code>.</li>
	 * </ul>
	 * 
	 * The method found following this algorithm is then returned.
	 * If no method has been found, returns <code>null</code>.
	 * @param klazz
	 * @param methodNames
	 * @param arguments
	 * @return
	 */
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
