package org.sonata.framework.control.invoker;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.exceptions.ParsingException;

public class XMLInvokerDAO extends InvokerDAO {
	
	private static final String XML_FILE_DEFAULT_PATH = "SOConnections.xml" ;
	
	private String xmlFilePath = null ;
	
	private static List<Element> dataStructure ;

	public void setXMLFilePath(final String filePath) {
		xmlFilePath = filePath ;
	}
	
	public boolean parseXmlFile() throws ParsingException {
		loadDataStructure() ;
		try {
			parseReferences() ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true ;
	}
	
	private void loadDataStructure() throws ParsingException {
		if (xmlFilePath == null) {
			xmlFilePath = XML_FILE_DEFAULT_PATH ;
		}
		Document xmlData;
		try {
			// TODO Eventually set the validation to true
			xmlData = new SAXBuilder(false).build(xmlFilePath);
			dataStructure = (List<Element>) xmlData.getRootElement().getChildren(
					"SOConnection");
		} catch (JDOMException e) {
			throw new ParsingException("\nMalformed XML file.\nTrace :\n"
					+ e.getMessage()) ;
		} catch (IOException e) {
			throw new ParsingException("\nCould not load connection file.\nTrace :\n"
							+ e.getMessage());
		}
	}
	
	private void parseReferences() throws ParsingException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		Class<SymphonyObject> sourceClass = null ;
		Class<ConnectionTranslation> translationClass = null ;
		
		for (Element element : dataStructure) {
				BrokerReference newReference = new BrokerReference() ;
				
				String sourceTypeString = element.getChild("source").getAttributeValue("type") ;
				ReferenceType sourceType = ReferenceType.valueOf(sourceTypeString) ;
				sourceClass = (Class<SymphonyObject>) classLoader.loadClass(element.getChild("source").getAttributeValue("name")) ;
				newReference.setSource(new ReferenceElement(sourceClass, sourceType)) ;
				
				for (Element dest : (List<Element>)element.getChildren("destination")) {
					Class<SymphonyObject> clazz = (Class<SymphonyObject>) classLoader.loadClass(dest.getAttributeValue("name")) ;
					newReference.addDestination(new ReferenceElement(clazz, ReferenceType.valueOf(dest.getAttributeValue("type")))) ;
				}
				
				Element proxyElement = element.getChild("proxy") ;
				Class<ProcessObject> proxy = null ;
				// If the Invoker is in unitTesting mode, this attribute could be undefined
				if (proxyElement != null) {
					proxy = (Class<ProcessObject>) classLoader.loadClass(proxyElement.getAttributeValue("name")) ;
				}
				newReference.setProxy(proxy) ;
				
				translationClass = (Class<ConnectionTranslation>) classLoader.loadClass(element.getChild("translation").getAttributeValue("name"));
				newReference.setTranslation(translationClass) ;
				
				theReferences.add(newReference) ;

		}
		
	}
}
