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

public class JSONInvokerDAO extends InvokerDAO {

	private JSONObject aParser ;
	
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
	
	public void parseJSONText(String aText) throws JSONException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		aParser = new JSONObject(aText) ;
		JSONArray theConnections = aParser.getJSONArray("SOConnections") ;
		
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
