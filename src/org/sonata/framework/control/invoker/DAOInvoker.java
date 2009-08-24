package org.sonata.framework.control.invoker;

import static org.sonata.framework.common.ReferenceType.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.exceptions.ParsingException;

class DAOInvoker {
	private static DAOInvoker uneInstanceDAOInvoker ;
	
	private static List<Element> structureDonnees ;
	
	final static DAOInvoker instance = newInstance() ;
	
	private String pathFichier ;
	
	static DAOInvoker newInstance() {
		if (uneInstanceDAOInvoker == null) uneInstanceDAOInvoker = new DAOInvoker  () ;
		return uneInstanceDAOInvoker ;
	}
	
	void chargerXML(String pathFichier) {
		this.pathFichier = pathFichier;
		File fichier = new File(pathFichier);
		Document xmlData;
		try {
			xmlData = new SAXBuilder(false).build(fichier);
			structureDonnees = (List<Element>) xmlData.getRootElement().getChildren(
					"SOConnection");
		} catch (JDOMException e) {
			Logger.getAnonymousLogger().severe(
					"\nCould not load connection file.\nTrace :\n"
							+ e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().severe(
					"\nCould not load connection file.\nTrace :\n"
							+ e.getMessage());
		}
	}
	
	/**
	 * Effectue le parsage du fichier de connexions, et en renvoie la liste
	 * @return
	 * @throws Exception 
	 */
	List<BrokerReference> getReferenceConnections() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		Class<SymphonyObject> sourceClass = null ;
		Class<ConnectionTranslation> wrapperClass = null ;
		
		
		if (structureDonnees == null)
		{
			throw new ParsingException("From file: " + pathFichier + "\nLa structure de donn�es charg�e est vide") ;
		}
		ArrayList<BrokerReference> brkConnList = new ArrayList<BrokerReference> () ;
		// Parcours des �l�ments OMConnection
		for (Element element : structureDonnees) {
			
			// Pour chaque OMConnection, on r�cup�re la source, LES destinations, le wrapper
			// sous forme de cha�nes de caract�res.
			
				// Parsage du ReferenceType de la source
				ReferenceType sourceType = null ;
				String sourceTypeString = element.getChild("source").getAttributeValue("type") ;
				if (sourceTypeString.matches("OIE"))
					sourceType = OIE ;
				else if (sourceTypeString.matches("OIP"))
					sourceType = OIP ;
				else if (sourceTypeString.matches("OME"))
					sourceType = OME ;
				else if (sourceTypeString.matches("OMP"))
					sourceType = OMP ;
				else {
					throw new ParsingException("Parsing error from file: " + pathFichier) ;
				}
				
				sourceClass = (Class<SymphonyObject>) classLoader.loadClass(element.getChild("source").getAttributeValue("name")) ;
				
				List<Class<SymphonyObject>> destination = new ArrayList<Class<SymphonyObject>> () ;
				List<Element> destList = element.getChildren("destination") ;
				
				
				for (Element dest : destList) {
					Class<SymphonyObject> clazz = (Class<SymphonyObject>) classLoader.loadClass(dest.getAttributeValue("name")) ;
					
					destination.add(clazz) ;
				}
				
				Element proxyElement = element.getChild("proxy") ;
				
				
				Class<ProcessObject> proxy = null ;
				// If the Invoker is in unitTesting mode, this attribute could be undefined
				if (proxyElement != null) {
					proxy = (Class<ProcessObject>) classLoader.loadClass(proxyElement.getAttributeValue("name")) ;
				}
				
				wrapperClass = (Class<ConnectionTranslation>) classLoader.loadClass(element.getChild("translation").getAttributeValue("name"));
				
				// TODO Temporally disabled
//				brkConnList.add(new BrokerReference(sourceClass,
//													sourceType,
//													destination,
//													proxy,
//													wrapperClass)) ;


		}
		return  brkConnList ;
		
	}

}
