package org.sonata.framework.control.invoker;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import static org.sonata.framework.control.request.RequestState.*;


/**
 * 
 * @author Guillaume Godet-Bar
 *
 */
/*
 *	TODO Supprimer la référence à l'OIP dans l'aspect. On doit pouvoir 
 *	acquérir la bonne instance à partir de la description de la connexion
 *	(par exemple en obtenant l'instance depuis la AbstractEntityFactory !)
 */
/*
 *	TODO Déplacer la gestion du fichier XML dans l'AbstractInitializer.
 *	C'est probablement la cause du manque de cohésion de la classe, qui 
 *	devrait n'accepter qu'une liste de BrokerReferences ?
 */
/*
 * 	TODO Supprimer la méthode register, qui est redondante par rapport à
 * 	la même méthode dans la classe AbstractEntityFactory !
 * 	On doit pouvoir remplacer la méthode par une encapsulation plus simple
 * 	de la méthode bind
 */
public class Invoker {

	private InvokerDAO dao ;
	
	private static final String XML_FILE_DEFAULT_PATH = "SOConnections.xml" ;
	
	private String xmlFilePath = null ;
	
	private static final Logger logger = Logger.getLogger("control.invoker.Invoker") ;
	
	private static Invoker instance ;
	
	/**
	 * Liste des Objets Symphony enregistrés auprès de l'Invoker (Objets Entité
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
	 * Retour de la requête
	 */
	private transient Object returnObject ;
	
	/**
	 * Flag pour signalement d'un retour de requête
	 */ 
	private transient boolean hasReturnedObject ;
	
	/**
	 * Liste des connections AUTORISEES (pas de connexions effectives...)
	 * On répertorie ci-desssous aussi bien les connexions entre OME/OIE qu'entre
	 * OMP/OIP (mêmes mécanismes --> connexion orientée)
	 */ 
	private final transient Map<String, BrokerReference> referenceTable ;
	
	/**
	 * Liste des connections EFFECTIVES
	 * Par exemple lors du traitement de la requête de création
	 * Contient aussi bien les O(M/I)E que les O(M/I)P
	 */
	private transient Map<SymphonyObject, ConnectionTranslation> connectionTable ;

	private boolean isUnitTesting;
	

	protected Invoker() {
		logger.setLevel(Level.FINE);
		
		requestStack = new Stack<Request>() ;
		
		oELookupTable = new HashMap<Class<?>, List<EntityObject>>() ;
		oPLookupTable = new HashMap<Class<?>, List<ProcessObject>>() ;
		
		connectionTable = new HashMap<SymphonyObject, ConnectionTranslation>() ;
		// Lors de l'instanciation de l'Invoker, il est nécessaire de lire
		// le fichier contenant toutes les connexions entre OME et OIE
		// ainsi qu'entre OMP et OIP
		// (ainsi que le nom du wrapper associé)
		referenceTable = new HashMap<String, BrokerReference>() ;
		
	}
	
	public synchronized static Invoker getInstance() {
		if (instance == null)
		{
			instance = new Invoker() ;		
		}
		return instance ;
	}
	
	public void setDao(InvokerDAO aDao) {
		this.dao = aDao ;
	}
	
	/**
	 * Note that the reference should only be registered once for every index key.
	 * Reloading a reference while connections with the old reference are still 
	 * alive would result in unpredictable behaviour.
	 * @param reference
	 * @return
	 */
	protected boolean registerConnection(BrokerReference reference) {
		if (referenceTable.containsValue(reference)) return false ;
		
		referenceTable.put(reference.getIndex(), reference) ;
		
		return true ;
	}
	
//	if (xmlFilePath == null) {
//		DAOInvoker.instance.chargerXML(XML_FILE_DEFAULT_PATH) ;
//	} else {
//		DAOInvoker.instance.chargerXML(xmlFilePath) ;
//	}
//	
//	List<BrokerReference> connectionList;
//	try {
//		connectionList = DAOInvoker.instance.getReferenceConnections();
//		
//		for (BrokerReference element : connectionList) {
//			referenceTable.put(element.getIndex(), element) ;
//		}
//		connectionTable = new HashMap<SymphonyObject, ConnectionTranslation> () ;
//	} catch (Exception e) {
//		logger.severe("There was a problem fetching the connection list\n" + e.getMessage());
//	}
	
//	public void setXMLFilePath(final String filePath) {
//		xmlFilePath = filePath ;
//	}
	
	public int loadConnections() throws IOException {
		if (dao == null) throw new IllegalStateException("The DAO should be defined.") ;
		for (BrokerReference aRef : dao.getBrokerReferences()) {
			if (!registerConnection(aRef)) throw new IOException("Error occurred while registering a connection") ;
		}
		return referenceTable.size() ;
	}
	
	public Request createRequest(final EntityObject proc, final String operationName, final ProcessObject proxy) throws RequestOverlapException {

		if (currentRequest != null && currentRequest.getRequestState() != SENT) throw new RequestOverlapException() ;
		
		currentRequest = new RequestImpl(proc, operationName, proxy) ;
		requestStack.push(currentRequest) ;
		
		logger.fine("Creating the request for object "+proc+" with operation " + operationName +  "\nRequest ID: " + currentRequest) ;
		return currentRequest ;

	}
	
	public boolean sendRequest() throws NoSuchMethodException, InvocationTargetException {
		currentRequest.nextState() ; // Request should be in state 'SENT'
//		hasReturnedObject = false ;
		
		logger.fine(
				
				"Launching the request processing call for: "
						+ currentRequest);	
		
		SymphonyObject sobject = currentRequest.getAssociatedSymphonyObject() ; // We get the caller's id

		try {
			validateConnection(sobject) ;
		} catch (InvalidSOConnection e) {
			logger.severe(e.getMessage());
			return false ;
		}
		invokeMethod(sobject);
		currentRequest.nextState() ; // Request should be in state 'RESPONSE_RECEIVED'
//		currentRequest.setHasReturnValue(hasReturnedObject) ;	// Apparently not supported!
		currentRequest.setReturnValue(returnObject) ;
		

		// Once the request is processed, we pop it out from the stack
		requestStack.pop() ;
		currentRequest = (requestStack.empty()) ? null : requestStack.peek() ;
		
		return true ;
	}
	
//	public boolean requestHasReturnedObject() {
//		return currentRequest.getRequestState() == RESPONSE_RECEIVED && hasReturnedObject ;
//	}
	
//	public Object getReturnObject() throws IllegalStateException {
//		if (requestHasReturnedObject()) {
//		return returnObject;}
//		else {
//			throw new IllegalStateException("La commande est inappropriée") ;
//		}
//	}
	

	
	// Vérifications de la cohérence, de la complétude etc.
	public boolean register(final SymphonyObject obj) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		
		String studiedClass = new String (obj.getClass().getName()) ;
		String classIName = studiedClass.substring(0, studiedClass.length() - 4) ;
		Class<?> classInterface = null ;
		
		logger.finest("Class being processed: " + studiedClass) ;	// Normalement une classe en 'Impl'
		
		
		//		 Pour valider l'OME (ou l'OMP), celui-ci doit implémenter EntityObject (ProcessObject)
		try {
			
			classInterface = classLoader.loadClass(classIName);
		
			if (obj instanceof EntityObject)
			{
				logger.fine(studiedClass + " SOE is valid") ;

					List<EntityObject> objectList = oELookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<EntityObject>() ;
						
					}
					// Should not register same object twice !					
					if (objectList.contains(obj)) {
						return false ;
					}
					
					objectList.add((EntityObject) obj) ;
					oELookupTable.put(classInterface, objectList) ;

			} else if (obj instanceof ProcessObject) {
				// Mécanisme d'inscription de l'O(M/I)P
				logger.fine(studiedClass + " SOP is valid") ;
				
					List<ProcessObject> objectList = oPLookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<ProcessObject>() ;
						
					}
					
					// Should not register same object twice !
					if (objectList.contains(obj)) {
						return false ;
					}
					
					objectList.add((ProcessObject) obj) ;
					oPLookupTable.put(classInterface, objectList) ;

			} else {
				logger.warning("Invalid Symphony Object: " + studiedClass) ;
			}
		} catch (ClassNotFoundException e1) {
			logger.warning("An error occurred while registering the object " +obj) ;
			logger.severe("The class does not exist: " + e1.getMessage()) ;
			return false ;
		}
		
		// À ce point, on peut inscrire l'objet dans la table de lookup
		// Pour l'instant, on n'inscrit que le nom de l'OME et la référence
		// À terme, il sera p-ê nécessaire d'exploiter la signature de la classe
		// (optimisation)
		// À partir de là, vérifier s'il existe une requête en cours à 
		// laquelle associer l'Objet Symphony tout juste enregistré (en tant
		// que destination !!!!)
		SymphonyObject sourceObject = getReqSourceObject(obj) ;
		if (sourceObject != null) {
			bind(sourceObject, obj) ;
		}
		
		return true ;
			
	}

	private SymphonyObject getReqSourceObject(final SymphonyObject obj) {
		/*
		 * 1. 	On récupère la requête courante (la requête doit être en cours
		 * 		de traitement)
		 * 2.	On vérifie que la liste des classes destination correspond bien
		 * 		à l'objet traité
		 * 3.	Sachant que l'objet vient juste d'être créé, déclarer un bind
		 * 		entre l'objet source de la requête en cours et l'objet courant
		 */
		
		SymphonyObject returnValue = null;
		if (currentRequest != null
				&& currentRequest.getRequestState().equals(SENT)) {
			SymphonyObject sourceObject = currentRequest
					.getAssociatedSymphonyObject();

			BrokerReference brkRef = referenceTable
					.get(getSObjectName(sourceObject));

			for (ReferenceElement singleReference : brkRef.getDestinations()) {
				if (singleReference.klazz.isAssignableFrom(obj.getClass())) {
					returnValue = sourceObject ;
					break ;
				}
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
	
	
	// TODO Récupérer à la fois le proxy de la connexion ainsi que l'objet symphony
	// destination, pour la validation.
	// Implique d'avoir D'ABORD géré le lien OI-OM
	// TODO Gérer l'instanciation du proxy, ou la récupération de l'instance !
	private void validateConnection(final SymphonyObject proc) throws InvalidSOConnection {
		// Règle structurelle : si proc est un Objet Entité et 
		
		
		// On acquiert la Translation associée à cet appel
		// On vérifie s'il existe déjà une connexion
		// associée à ce processus
		
		if (connectionTable.get(proc) == null) {
			// On vérifie le Wrapper auquel proc est associé
			// D'abord, on doit s'assurer que la classe de proc existe dans la
			// table de références
			String nomObjetSymphony = getSObjectName(proc) ;
			BrokerReference brkRef = referenceTable.get(nomObjetSymphony);
			
			if (brkRef == null) {
				throw new InvalidSOConnection("L'objet "
						+ proc
						+ " n'existe pas dans la table des connexions et n'est pas présent dans"
						+ " la table de référence des connexions") ;
			}
			
			// La connexion est invalide si le proxy de l'objet ne correspond pas à celui
			// de la référence. Il est à noté que les Objet Processus ne nécessitent pas cette
			// vérification
			// TODO voir si la vérification est nécessaire dans le cadre des tests unitaires...
			if ((!isUnitTesting) && !(proc instanceof ProcessObject) && !brkRef.getProxy().getName().equals(getSObjectName(currentRequest.getProxy()))) {
				throw new InvalidSOConnection("Le proxy " + getSObjectName(currentRequest.getProxy()) + "ne correspond pas à"
						+ " la référence : " + brkRef.getProxy().getName()) ;
			}
			
				logger.fine("La classe " + nomObjetSymphony + " de " + proc
						+ "est renseignée dans la table de référence");

				// Il s'agit ensuite de récupérer l'instance des processus et
				// translation associés
				// (au besoin, il sera nécessaire de créer le processus)

				
					Class<ConnectionTranslation> translationClass = (Class<ConnectionTranslation>) brkRef.getTranslation();
					Constructor<ConnectionTranslation> constructor = null ;
					ConnectionTranslation translationObject = null ;
				
				try {
					//	 On récupère le constructeur du wrapper
					constructor = translationClass
							.getConstructor(SymphonyObject.class, ProcessObject.class);
					
				} catch (SecurityException e) {
					logger.severe(
							"Erreur de sécurité (accès à la méthode)\n"
									+ e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.severe(
							"La méthode n'existe pas\n" + e.getMessage());
				}

				try {
					if (constructor != null) {
					translationObject = (ConnectionTranslation) constructor
							.newInstance(proc, currentRequest.getProxy());
					}
					

				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(
							"La translation " + brkRef.getTranslation()
									+ " n'a pas pu être instanciée\n"
									+ e.getMessage());
				} 

				connectionTable.put(proc, translationObject);
				logger.fine(
								"La connection entre "
										+ proc
										+ " et ses cibles est assurée au travers de la translation "
										+ translationObject
										+ "\nEnregistrement dans la connectionTable réussi");
			}
		
	}
	
	/**
	 * Invokes the method described in the proc parameter. Throws NoSuchMethodException if 
	 * no matching method exists. Note that the invoked method should be <code>public</code>.
	 * <br />
	 * Primitive arguments are unwrapped if necessary, however the invoker does not support mixing
	 * wrapped and unwrapped arguments.
	 * @param proc
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException 
	 */
	private void invokeMethod(final SymphonyObject proc) throws NoSuchMethodException, InvocationTargetException {

		// A ce point, la classe Wrapper a été instanciée (objet
		// wrapperObject)
		// Il suffit à présent de passer la main au wrapper

		String methodName = currentRequest.getOpName();

		ConnectionTranslation liveConnection = connectionTable.get(proc);

		Method method = null;

		// Liste des paramètres convertis (voir ci-dessous)
		Class<?>[] convertedPTypes = new Class[currentRequest.getParamTypeArray().length];

		// Le principe ci-dessous est de convertir tous les ParamTypes
		// dépendant d'Objets Symphony (class Impl) vers leur superclasse

		/*
		 * Mecanisme :
		 *  - Si l'objet implémente EntityObject ou bien ProcessObject, on
		 * extrait le nom de l'Objet Symphony, - Sinon on conserve le
		 * paramètre tel quel
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
			// À ce point les paramètres ont été adaptés
		try {
			method = liveConnection.getClass().getMethod(
					methodName, (Class[]) convertedPTypes);
		} catch (NoSuchMethodException e) {
			logger.info("Method does not exist. Trying to unwrap primitive arguments") ;
			
			List<Class<?>> newArguments = new LinkedList<Class<?>>() ;
			
			for (Class<?> anArgument : convertedPTypes) {
				if (anArgument.isAssignableFrom(Integer.class)) {
					newArguments.add(int.class) ;
				} else if (anArgument.isAssignableFrom(Short.class)) {
					newArguments.add(short.class) ;
				} else if (anArgument.isAssignableFrom(Byte.class)) {
					newArguments.add(byte.class) ;
				} else if (anArgument.isAssignableFrom(Long.class)) {
					newArguments.add(long.class) ;
				} else if (anArgument.isAssignableFrom(Double.class)) {
					newArguments.add(double.class) ;
				} else if (anArgument.isAssignableFrom(Float.class)) {
					newArguments.add(float.class) ;
				} else if (anArgument.isAssignableFrom(Character.class)) {
					newArguments.add(char.class) ;
				} else if (anArgument.isAssignableFrom(Boolean.class)) {
					newArguments.add(boolean.class) ;
				} else {
					newArguments.add(anArgument) ;
				}
			}
			
			method = liveConnection.getClass().getMethod(
						methodName, (Class[]) newArguments.toArray(convertedPTypes));
		}
		
		try {
			returnObject = method.invoke(liveConnection,
					currentRequest.getParamArray().toArray());
			hasReturnedObject = (returnObject != null);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}
	
	

	
	/**
	 * Explicit binding between a BO instance and an IO instance. A ConnectionTranslation is setup between
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
		
		ConnectionTranslation connection = connectionTable.get(source);
	
		for (ReferenceElement singleRef : brokerRef.getDestinations()) {
			Class<?> aReferenceClass = singleRef.klazz ;
			if (aReferenceClass.isAssignableFrom(target.getClass())) {
					connection.addDestination(aReferenceClass, target) ;
			}
		}	
		
		logger.fine("connectionTable: new link between " + source + " and " + target) ;
	}

	public void setUnitTesting(boolean trueFalse) {
		isUnitTesting = trueFalse ;
	}
	
}
