package org.sonata.framework.control.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.control.exceptions.InvalidSOConnection;
import org.sonata.framework.control.exceptions.RequestOverlapException;
import org.sonata.framework.control.request.Request;
import org.sonata.framework.control.request.RequestImpl;
import org.sonata.framework.control.request.RequestState;


/**
 * 
 * @author Guillaume Godet-Bar
 *
 */
public final class Invoker {

	private static final String XML_FILE_DEFAULT_PATH = "SOConnections.xml" ;
	
	private String xmlFilePath = null ;
	
	private static final Logger logger = Logger.getLogger("control.invoker.Invoker") ;
	
	/**
	 * Mutex utilis� dans la m�thode newInstance() afin de garantir la thread-safety
	 * du singleton (NB : doit �tre d�crit AVANT instance, c.f. ordre s�quentiel de la
	 * compilation des fragments statiques de la classe)
	 */ 
	private static final Object lock = new Object() ;
	
	public static Invoker instance = newInstance() ;
	
	/**
	 * Liste des Objets Symphony enregistr�s aupr�s de l'Invoker (Objets Entit�
	 * et Objets Processus).
	 * 
	 */
	private final Map<Class<?>, List<EntityObject>> oELookupTable ;
	private final Map<Class<?>, List<ProcessObject>>oPLookupTable ;
	
	/**
	 * Stack of requests currently being processes. Each completed request (i.e., 
	 * in a RESPONSE_RECEIVED state) is popped from the stack, the current request
	 * being the one at the top of the stack.
	 */
	private transient Stack<Request> requestStack ;

	private transient Request currentRequest ;
	
	/**
	 * Retour de la requ�te
	 */
	private transient Object returnObject ;
	
	/**
	 * Flag pour signalement d'un retour de requ�te
	 */ 
	private transient boolean hasReturnedObject ;
	
	/**
	 * Liste des connections AUTORISEES (pas de connexions effectives...)
	 * On r�pertorie ci-desssous aussi bien les connexions entre OME/OIE qu'entre
	 * OMP/OIP (m�mes m�canismes --> connexion orient�e)
	 */ 
	private final transient Map<String, BrokerReference> referenceTable ;
	
	/**
	 * Liste des connections EFFECTIVES
	 * Par exemple lors du traitement de la requ�te de cr�ation
	 * Contient aussi bien les O(M/I)E que les O(M/I)P
	 */
	private transient Map<SymphonyObject, BrokerConnection> connectionTable ;

	private boolean isUnitTesting;
	

	private Invoker() {
		super();
		logger.setLevel(Level.FINE);
		
		requestStack = new Stack<Request>() ;
		
		oELookupTable = new HashMap<Class<?>, List<EntityObject>>() ;
		oPLookupTable = new HashMap<Class<?>, List<ProcessObject>>() ;
		
		
		// Lors de l'instanciation de l'Invoker, il est n�cessaire de lire
		// le fichier contenant toutes les connexions entre OME et OIE
		// ainsi qu'entre OMP et OIP
		// (ainsi que le nom du wrapper associ�)
		referenceTable = new HashMap<String, BrokerReference>() ;
		
	}
	
	private static Invoker newInstance() {
		synchronized(lock) {
			if (instance == null)
			{
				instance = new Invoker() ;		
			}
			return instance ;
		}
	}
	
	public void setXMLFilePath(final String filePath) {
		xmlFilePath = filePath ;
	}
	
	public int loadConnections() {
		if (xmlFilePath == null) {
			DAOInvoker.instance.chargerXML(XML_FILE_DEFAULT_PATH) ;
		} else {
			DAOInvoker.instance.chargerXML(xmlFilePath) ;
		}
		
		List<BrokerReference> connectionList;
		try {
			connectionList = DAOInvoker.instance.getReferenceConnections();
			
			for (BrokerReference element : connectionList) {
				referenceTable.put(element.source.getName(), element) ;
			}
			connectionTable = new HashMap<SymphonyObject, BrokerConnection> () ;
		} catch (Exception e) {
			Logger.getAnonymousLogger().severe("There was a problem fetching the connection list\n" + e.getMessage());
		}
		
		return referenceTable.size() ;
	}
	
	public Request createRequest(final SymphonyObject proc, final String operationName, final ProcessObject proxy) throws RequestOverlapException {

		currentRequest = new RequestImpl(proc, operationName, proxy) ;
		requestStack.push(currentRequest) ;
		
		logger.fine("Creating the request for object "+proc+" with operation " + operationName +  "\nRequest ID: " + currentRequest) ;
		return currentRequest ;

	}
	
	public void sendRequest() {
		currentRequest.nextState() ; // Request should be in state 'SENT'
		hasReturnedObject = false ;
		
		logger.fine(
				
				"Launching the request processing call for: "
						+ currentRequest);	
		
		SymphonyObject sobject = currentRequest.getAssociatedSymphonyObject() ; // We get the caller's id

		try {
			validateConnection(sobject) ;
		} catch (InvalidSOConnection e) {
			logger.severe(e.getMessage());
		}
		invokeMethod(sobject);
		currentRequest.nextState() ; // Request should be in state 'RESPONSE_RECEIVED'
		currentRequest.setHasReturnValue(hasReturnedObject) ;
		currentRequest.setReturnValue(returnObject) ;
		

		// Once the request is processed, we pop it out from the stack
		requestStack.pop() ;
		if (!requestStack.empty()) {
			currentRequest = requestStack.peek() ;
		} else {
			currentRequest = null ;
		}
		
	}
	
//	public boolean requestHasReturnedObject() {
//		return currentRequest.getRequestState() == RESPONSE_RECEIVED && hasReturnedObject ;
//	}
	
//	public Object getReturnObject() throws IllegalStateException {
//		if (requestHasReturnedObject()) {
//		return returnObject;}
//		else {
//			throw new IllegalStateException("La commande est inappropri�e") ;
//		}
//	}
	

	
	// V�rifications de la coh�rence, de la compl�tude etc.
	public boolean register(final SymphonyObject obj) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		
		String studiedClass = new String (obj.getClass().getName()) ;
		String classIName = studiedClass.substring(0, studiedClass.length() - 4) ;
		Class<?> classInterface = null ;
		
		logger.finest("Class being processed: " + studiedClass) ;	// Normalement une classe en 'Impl'
		
		
		//		 Pour valider l'OME (ou l'OMP), celui-ci doit impl�menter EntityObject (ProcessObject)
		try {
			
			classInterface = classLoader.loadClass(classIName);
		
			if (obj instanceof EntityObject)
			{
				logger.fine(studiedClass + " SOE is valid") ;

					List<EntityObject> objectList = oELookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<EntityObject>() ;
						
					}
					objectList.add((EntityObject) obj) ;
					oELookupTable.put(classInterface, objectList) ;

			} else if (obj instanceof ProcessObject) {
				// M�canisme d'inscription de l'O(M/I)P
				logger.fine(studiedClass + " SOP is valid") ;
				
					List<ProcessObject> objectList = oPLookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<ProcessObject>() ;
						
					}
					objectList.add((ProcessObject) obj) ;
					oPLookupTable.put(classInterface, objectList) ;

			} else {
				logger.warning("Invalid Symphony Object: " + studiedClass) ;
			}
		} catch (ClassNotFoundException e1) {
			logger.warning("An error occurred while registering the object " +obj) ;
			Logger.getAnonymousLogger().severe("The class does not exist: " + e1.getMessage()) ;
			return false ;
		}
		
		// � ce point, on peut inscrire l'objet dans la table de lookup
		// Pour l'instant, on n'inscrit que le nom de l'OME et la r�f�rence
		// � terme, il sera p-� n�cessaire d'exploiter la signature de la classe
		// (optimisation)
		// � partir de l�, v�rifier s'il existe une requ�te en cours � 
		// laquelle associer l'Objet Symphony tout juste enregistr� (en tant
		// que destination !!!!)
		SymphonyObject sourceObject = getReqSourceObject(obj) ;
		if (sourceObject != null) {
			bind(sourceObject, obj) ;
		}
		
		return true ;
			
	}

	private SymphonyObject getReqSourceObject(final SymphonyObject obj) {
		/*
		 * 1. 	On r�cup�re la requ�te courante (la requ�te doit �tre en cours
		 * 		de traitement)
		 * 2.	On v�rifie que la liste des classes destination correspond bien
		 * 		� l'objet trait�
		 * 3.	Sachant que l'objet vient juste d'�tre cr��, d�clarer un bind
		 * 		entre l'objet source de la requ�te en cours et l'objet courant
		 */
		
		SymphonyObject returnValue = null;
		if (currentRequest != null
				&& currentRequest.getRequestState().equals(RequestState.SENT)) {
			SymphonyObject sourceObject = currentRequest
					.getAssociatedSymphonyObject();

			BrokerReference brkRef = referenceTable
					.get(getSObjectName(sourceObject));

			boolean isObjInRef = false;
			for (Class<SymphonyObject> singleClass : brkRef.destinations) {
				isObjInRef |= getSObjectName(obj).equals(singleClass.getName());
			}

			if (isObjInRef) {
				returnValue = sourceObject;
			}

		}
		return returnValue;
	}
	
	private String getSObjectName(final SymphonyObject obj) {
		String nomObjetSymphony = obj.getClass().getName();
		nomObjetSymphony = nomObjetSymphony.substring(0, nomObjetSymphony
				.length() - 4);
		return nomObjetSymphony ;
	}
	
	
	// TODO R�cup�rer � la fois le proxy de la connexion ainsi que l'objet symphony
	// destination, pour la validation.
	// Implique d'avoir D'ABORD g�r� le lien OI-OM
	// TODO G�rer l'instanciation du proxy, ou la r�cup�ration de l'instance !
	private void validateConnection(final SymphonyObject proc) throws InvalidSOConnection {
		// R�gle structurelle : si proc est un Objet Entit� et 
		
		
		// On acquiert la Translation associ�e � cet appel
		// On v�rifie s'il existe d�j� une connexion
		// associ�e � ce processus
		
		if (connectionTable.get(proc) == null) {
			// On v�rifie le Wrapper auquel proc est associ�
			// D'abord, on doit s'assurer que la classe de proc existe dans la
			// table de r�f�rences
			String nomObjetSymphony = getSObjectName(proc) ;
			BrokerReference brkRef = referenceTable.get(nomObjetSymphony);
			
			if (brkRef == null) {
				throw new InvalidSOConnection("L'objet "
						+ proc
						+ " n'existe pas dans la table des connexions et n'est pas pr�sent dans"
						+ " la table de r�f�rence des connexions") ;
			}
			
			// La connexion est invalide si le proxy de l'objet ne correspond pas � celui
			// de la r�f�rence. Il est � not� que les Objet Processus ne n�cessitent pas cette
			// v�rification
			// TODO voir si la v�rification est n�cessaire dans le cadre des tests unitaires...
			if ((!isUnitTesting) && !(proc instanceof ProcessObject) && !brkRef.proxy.getName().equals(getSObjectName(currentRequest.getProxy()))) {
				throw new InvalidSOConnection("Le proxy " + getSObjectName(currentRequest.getProxy()) + "ne correspond pas �"
						+ " la r�f�rence : " + brkRef.proxy.getName()) ;
			}
			
				logger.fine("La classe " + nomObjetSymphony + " de " + proc
						+ "est renseign�e dans la table de r�f�rence");

				// Il s'agit ensuite de r�cup�rer l'instance des processus et
				// translation associ�s
				// (au besoin, il sera n�cessaire de cr�er le processus)

				
					Class<ConnectionTranslation> translationClass = (Class<ConnectionTranslation>) brkRef.translation;
					Constructor<ConnectionTranslation> constructor = null ;
					ConnectionTranslation translationObject = null ;
				
				try {
					//	 On r�cup�re le constructeur du wrapper
					constructor = translationClass
							.getConstructor(SymphonyObject.class, ProcessObject.class);
					
				} catch (SecurityException e) {
					Logger.getAnonymousLogger().severe(
							"Erreur de s�curit� (acc�s � la m�thode)\n"
									+ e.getMessage());
				} catch (NoSuchMethodException e) {
					Logger.getAnonymousLogger().severe(
							"La m�thode n'existe pas\n" + e.getMessage());
				}

				try {
					if (constructor != null) {
					translationObject = (ConnectionTranslation) constructor
							.newInstance(proc, currentRequest.getProxy());
					}
					

				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(
							"La translation " + brkRef.translation
									+ " n'a pas pu �tre instanci�e\n"
									+ e.getMessage());
				} 
				
				BrokerConnection brkConn = new BrokerConnection();
				brkConn.setTranslation(translationObject);
				brkConn.setSource(proc);

				connectionTable.put(proc, brkConn);
				Logger.getLogger("control.invoker.Invoker")
						.fine(
								"La connection entre "
										+ proc
										+ " et ses cibles est assur�e au travers de la translation "
										+ translationObject
										+ "\nEnregistrement dans la connectionTable r�ussi");
			}
		
	}
	
	
	private void invokeMethod(final SymphonyObject proc) {

		// A ce point, la classe Wrapper a �t� instanci�e (objet
		// wrapperObject)
		// Il suffit � pr�sent de passer la main au wrapper

		String methodName = currentRequest.getOpName();

		BrokerConnection liveConnections = connectionTable.get(proc);

		Method method = null;

		// Liste des param�tres convertis (voir ci-dessous)
		Class<?>[] convertedPTypes = new Class[currentRequest.getParamTypeArray().length];
		try {
			// Le principe ci-dessous est de convertir tous les ParamTypes
			// d�pendant d'Objets Symphony (class Impl) vers leur superclasse

			/*
			 * Mecanisme :
			 *  - Si l'objet impl�mente EntityObject ou bien ProcessObject, on
			 * extrait le nom de l'Objet Symphony, - Sinon on conserve le
			 * param�tre tel quel
			 * 
			 */
			int compteur = 0;
			for (Class<?> singleClass : currentRequest.getParamTypeArray()) {
				boolean objectIsSO = false;

				for (Class<?> singleInterface : singleClass.getInterfaces()) {
					// Traitement de l'OS (parameterType)
					if (oELookupTable.get(singleInterface) != null
							|| oPLookupTable.get(singleInterface) != null) {
						convertedPTypes[compteur++] = singleInterface;
						objectIsSO = true;
						break;
					}
				}

				if (!objectIsSO) {
					convertedPTypes[compteur++] = singleClass;
				}

			}
			// � ce point les param�tres ont �t� adapt�s

			method = liveConnections.getTranslation().getClass().getMethod(
					methodName, (Class[]) convertedPTypes);

			returnObject = method.invoke(liveConnections.getTranslation(),
					currentRequest.getParamArray().toArray());

			hasReturnedObject = (returnObject != null);

		} catch (InvocationTargetException e) {
			Logger
					.getAnonymousLogger()
					.severe(
							"La m�thode : " + method.getName() +
							"\nappel�e dans le wrapper : " + liveConnections.getTranslation().getClass().getName() + 
							"\na renvoy� une exception\n");
							e.printStackTrace() ;
		} catch (Exception e) {
			Logger.getAnonymousLogger().severe(e.getMessage());
			e.printStackTrace();
		}

	}
	
	

	
	/**
	 * Explicit binding between a BO instance and an IO instance. A BrokerConnection is setup between
	 * the two objects and added to the connection table of the Invoker.
	 * 
	 * @param source the source Symphony Object
	 * @param target the target Symphony Object
	 */
	private void bind(final SymphonyObject source, final SymphonyObject target) {
		
		BrokerReference brokerRef = null ;
		
		for (Class<?> singleInterface : source.getClass().getInterfaces()) {
			// We get the interface name that should be that of the applicative services of the object
			// (i.e., we check if one of the interfaces has been registered in the referenceTable
			brokerRef = referenceTable.get(singleInterface.getName());
			if (brokerRef != null) { break ; }
		}
		
		BrokerConnection connection = connectionTable.get(source);
		
		// If the connection table does not have a reference, we prepare
		// a new connection
		// TODO That should not happen?!
		if (connection == null) {
			connection = new BrokerConnection() ;
			connectionTable.put(source, connection);
		} 
	
		for (Class<?> singleClass : brokerRef.destinations) {
			for (Class<?> targetInterface : target.getClass().getInterfaces()) {
				if (targetInterface.equals(singleClass)) {
					connection.getTranslation().addDestination((Class<SymphonyObject>) targetInterface, target) ;
					break ;
				}
			}
		}	
	
		
		logger.fine("connectionTable: new link between " + source + " and " + target) ;
	}

	public void setUnitTesting(boolean trueFalse) {
		isUnitTesting = trueFalse ;
	}
	
}
