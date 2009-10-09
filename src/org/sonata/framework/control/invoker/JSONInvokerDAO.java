package org.sonata.framework.control.invoker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;

/**
 * An implementation of the <code>InvokerDAO</code> abstract class, for
 * loading the Invoker's properties from JSON strings or files containing
 * a JSON string. The following structure is assumed:
 * 
 * <pre>
 * <code>
 * 		{ "SOConnections" : [
 * 			{	"source" : 
 * 					{"name" : qualified_name_of_the_source, "type" : type_of_the_source}
 * 				"destinations" : [
 * 						{"name" : qualified_name_of_the_destination, "type" : type_of_the_destination},
 * 						{...}
 * 					],
 * 				"translation" : qualified_name_of_the_translation
 * 			}
 * 		]}
 * </code>
 * </pre>
 * 
 * @author Guillaume Godet-Bar
 *
 */
public class JSONInvokerDAO extends InvokerDAO {

	/**
	 * The root object used for parsing the JSON string
	 */
	private JSONObject jsonParser ;
	
	/**
	 * Loads the file named <code>filename</code>, parses the content 
	 * and fills the field <code>theReferences</code> from the abstract
	 * class.
	 * 
	 * @param filename
	 * @throws IOException	if the file could not be found or read
	 * @throws JSONException if there was a parsing error
	 * @throws ClassNotFoundException if the classes to be loaded could not be found
	 */
	public void loadJSONFile(String filename) throws IOException, JSONException, ClassNotFoundException {
		File f = new File(filename) ;
		if (!f.exists() || !f.isFile()) throw new FileNotFoundException() ;
		FileReader fr = new FileReader(f) ;
		BufferedReader br = new BufferedReader(fr) ;
		StringBuilder sb = new StringBuilder("");
		while (br.ready()) {
				sb.append(br.readLine()) ;
		}
		parseJSONText(sb.toString()) ;
	}
	
	/**
	 * Parses the JSON string described in <code>aText</code> and fills
	 * the field <code>theReferences</code> from the abstract class.
	 * 
	 * @param aText
	 * @throws JSONException if there was a parsing error
	 * @throws ClassNotFoundException if the classes to be loaded could not be found
	 */
	public void parseJSONText(String aText) throws JSONException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		jsonParser = new JSONObject(aText) ;
		JSONArray theConnections = jsonParser.getJSONArray("SOConnections") ;
		
		for (int i = 0 ; i < theConnections.length() ; i++) {
			BrokerReference newBrokerReference = new BrokerReference() ;
			JSONObject aConnection = theConnections.getJSONObject(i) ;
			JSONObject theSource = aConnection.getJSONObject("source") ;
			newBrokerReference.setSource(classLoader.loadClass(theSource.getString("name")),
					ReferenceType.valueOf(theSource.getString("type"))) ;
			
			JSONArray theDestinations = aConnection.getJSONArray("destinations") ;
			
			for (int j = 0 ; j < theDestinations.length() ; j++) {
				JSONObject aDestination = theDestinations.getJSONObject(j) ;
				
				ReferenceElement destinationElement = new ReferenceElement(classLoader.loadClass(aDestination.getString("name")),
																	ReferenceType.valueOf(aDestination.getString("type"))) ;
				newBrokerReference.addDestination(destinationElement) ;
			}
			
			newBrokerReference.setTranslation((Class<? extends ConnectionTranslation>) classLoader.loadClass(aConnection.getString("translation"))) ;
			
			this.theReferences.add(newBrokerReference) ;
		}
	}
}
