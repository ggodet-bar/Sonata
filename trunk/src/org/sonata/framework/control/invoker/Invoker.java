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

import org.sonata.framework.common.*;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.process.AbstractProcessFactory;
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

	private static final Logger logger = Logger.getLogger("control.invoker.Invoker") ;
	
	/**
	 * Mutex utilisé dans la méthode newInstance() afin de garantir la thread-safety
	 * du singleton (NB : doit être décrit AVANT instance, c.f. ordre séquentiel de la
	 * compilation des fragments statiques de la classe)
	 */ 
	private static final Object lock = new Object() ;
	
	public static Invoker instance = newInstance() ;
	
	/**
	 * Liste des Objets Symphony enregistrés auprès de l'Invoker (Objets Entité
	 * et Objets Processus).
	 * 
	 */
	private final Map<Class<?>, List<EntityObject>> oELookupTable ;
	private final Map<Class<?>, List<ProcessObject>>oPLookupTable ;
	
	/**
	 * Pile des requêtes en cours. Chaque requête traitée (état RESPONSE_RECEIVED) est
	 * retirée du stack, et la requête courante devient celle au-dessus du stack.
	 */
	private transient Stack<Request> requestStack ;
	
	/**
	 * Requête courante
	 */ 
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
	private transient Map<SymphonyObject, BrokerConnection> connectionTable ;

	private boolean isUnitTesting;
	

	private Invoker() {
		super();
		logger.setLevel(Level.FINE);
		
		requestStack = new Stack<Request>() ;
		
		oELookupTable = new HashMap<Class<?>, List<EntityObject>>() ;
		oPLookupTable = new HashMap<Class<?>, List<ProcessObject>>() ;
		
		
		// Lors de l'instanciation de l'Invoker, il est nécessaire de lire
		// le fichier contenant toutes les connexions entre OME et OIE
		// ainsi qu'entre OMP et OIP
		// (ainsi que le nom du wrapper associé)
		referenceTable = new HashMap<String, BrokerReference>() ;
		DAOInvoker.instance.chargerXML("XMLFile/OMConnexions.xml") ;
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
		
	}
	
	public static Invoker newInstance() {
		synchronized(lock) {
			if (instance == null)
			{
				instance = new Invoker() ;		
			}
			return instance ;
		}
	}
	
	public Request createRequest(final SymphonyObject proc, final String operationName, final ProcessObject proxy) throws RequestOverlapException {

		currentRequest = new RequestImpl(proc, operationName, proxy) ;
		requestStack.push(currentRequest) ;
		
		logger.fine("Création de la requête par l'objet "+proc+" avec l'opération " + operationName +  "\nID de la requête : " + currentRequest) ;
		return currentRequest ;

	}
	
	public void sendRequest() {
		currentRequest.nextState() ; // Requête dans l'état 'SENT'
		hasReturnedObject = false ;
		
		logger.fine(
				
				"Lancement du thread de traitement de la requête "
						+ currentRequest);	
		
		SymphonyObject sobject = currentRequest.getAssociatedSymphonyObject() ; // On obtient la source de l'appel

		try {
			validateConnection(sobject) ;
		} catch (InvalidSOConnection e) {
			logger.severe(e.getMessage());
		}
		invokeMethod(sobject);
		currentRequest.nextState() ; // Requête dans l'état 'RESPONSE_RECEIVED'
		currentRequest.setHasReturnValue(hasReturnedObject) ;
		currentRequest.setReturnValue(returnObject) ;
		
		/*
		 * Une fois la requête remplie, on sort celle-ci de la pile
		 */
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
//			throw new IllegalStateException("La commande est inappropriée") ;
//		}
//	}
	

	
	// Vérifications de la cohérence, de la complétude etc.
	public void register(final SymphonyObject obj) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		
		String studiedClass = new String (obj.getClass().getName()) ;
		String classIName = studiedClass.substring(0, studiedClass.length() - 4) ;
		Class<?> classInterface = null ;
		
		logger.finest("Classe étudiée : " + studiedClass) ;	// Normalement une classe en 'Impl'
		
		
		//		 Pour valider l'OME (ou l'OMP), celui-ci doit implémenter EntityObject (ProcessObject)
		boolean validOME = false ;
		boolean validOMP = false ;
		try {
			
			classInterface = classLoader.loadClass(classIName);
			
			validOME = obj instanceof EntityObject ;
			validOMP = obj instanceof ProcessObject ;
		
			if (validOME)
			{
				logger.fine("OSE " + studiedClass + " est valide") ;

				
				Class<AbstractEntityFactory> cla = (Class<AbstractEntityFactory>) classLoader.loadClass(classIName + "Factory") ;
				// On vérifie que la factory appartient également à l'OME
				// (doit implémenter EntityFactory)
				boolean validFactory = false ;
				validFactory = cla.getSuperclass().getName().matches("org.sonata.framework.common.entity.AbstractEntityFactory");

				if (validFactory)
				{
					List<EntityObject> objectList = oELookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<EntityObject>() ;
						
					}
					objectList.add((EntityObject) obj) ;
					oELookupTable.put(classInterface, objectList) ;
					logger.finest("OSEFactory valide") ;
				}
				else {
					logger.warning("OSEFactory invalide : " + studiedClass) ;
				}
			} else if (validOMP) {
				// Mécanisme d'inscription de l'O(M/I)P
				logger.fine("OSP " + studiedClass + " est valide") ;
				Class<AbstractProcessFactory> cla = (Class<AbstractProcessFactory>) classLoader.loadClass(classIName + "Factory") ;
				
				// On vérifie que la factory appartient également à l'OMP
				// (doit implémenter ProcessFactory)
				boolean validFactory = false ;
				validFactory |= cla.getSuperclass().getName().matches("org.sonata.framework.common.process.AbstractProcessFactory") ;
				if (validFactory)
				{
					List<ProcessObject> objectList = oPLookupTable.get(classInterface) ;
					if (objectList == null) {
						objectList = new ArrayList<ProcessObject>() ;
						
					}
					objectList.add((ProcessObject) obj) ;
					oPLookupTable.put(classInterface, objectList) ;
					logger.finest("OSPFactory valide") ;
				}
				else {
					logger.warning("OSPFactory invalide") ;
				}
			} else {
				logger.warning("OS est invalide : " + studiedClass) ;
			}
		} catch (ClassNotFoundException e1) {
			logger.warning("Erreur lors de l'enregistrement de l'objet " +obj) ;
			Logger.getAnonymousLogger().severe("La classe étudiée n'existe pas : " + e1.getMessage()) ;
			return ;
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
			if ((!isUnitTesting) && !(proc instanceof ProcessObject) && !brkRef.proxy.getName().equals(getSObjectName(currentRequest.getProxy()))) {
				throw new InvalidSOConnection("Le proxy " + getSObjectName(currentRequest.getProxy()) + "ne correspond pas à"
						+ " la référence : " + brkRef.proxy.getName()) ;
			}
			
				logger.fine("La classe " + nomObjetSymphony + " de " + proc
						+ "est renseignée dans la table de référence");

				// Il s'agit ensuite de récupérer l'instance des processus et
				// translation associés
				// (au besoin, il sera nécessaire de créer le processus)

				
					Class<ConnectionTranslation> translationClass = (Class<ConnectionTranslation>) brkRef.translation;
					Constructor<ConnectionTranslation> constructor = null ;
					ConnectionTranslation translationObject = null ;
				
				try {
					//	 On récupère le constructeur du wrapper
					constructor = translationClass
							.getConstructor(SymphonyObject.class, ProcessObject.class);
					
				} catch (SecurityException e) {
					Logger.getAnonymousLogger().severe(
							"Erreur de sécurité (accès à la méthode)\n"
									+ e.getMessage());
				} catch (NoSuchMethodException e) {
					Logger.getAnonymousLogger().severe(
							"La méthode n'existe pas\n" + e.getMessage());
				}

				try {
					if (constructor != null) {
					translationObject = (ConnectionTranslation) constructor
							.newInstance(proc, currentRequest.getProxy());
					}
					

				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(
							"La translation " + brkRef.translation
									+ " n'a pas pu être instanciée\n"
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
										+ " et ses cibles est assurée au travers de la translation "
										+ translationObject
										+ "\nEnregistrement dans la connectionTable réussi");
			}
		
	}
	
	
	private void invokeMethod(final SymphonyObject proc) {

		// A ce point, la classe Wrapper a été instanciée (objet
		// wrapperObject)
		// Il suffit à présent de passer la main au wrapper

		String methodName = currentRequest.getOpName();

		BrokerConnection liveConnections = connectionTable.get(proc);

		Method method = null;

		// Liste des paramètres convertis (voir ci-dessous)
		Class<?>[] convertedPTypes = new Class[currentRequest.getParamTypeArray().length];
		try {
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

			method = liveConnections.getTranslation().getClass().getMethod(
					methodName, (Class[]) convertedPTypes);

			returnObject = method.invoke(liveConnections.getTranslation(),
					currentRequest.getParamArray().toArray());

			hasReturnedObject = (returnObject != null);

		} catch (InvocationTargetException e) {
			Logger
					.getAnonymousLogger()
					.severe(
							"La méthode : " + method.getName() +
							"\nappelée dans le wrapper : " + liveConnections.getTranslation().getClass().getName() + 
							"\na renvoyé une exception\n");
							e.printStackTrace() ;
		} catch (Exception e) {
			Logger.getAnonymousLogger().severe(e.getMessage());
			e.printStackTrace();
		}

	}
	
	

	
	/**
	 * Mise en relation explicite entre l'OI et l'OM. 
	 * Une BrokerConnection est établie entre les deux objets, qui sera 
	 * intégrée à la table de connexions de l'Invoker
	 * 
	 * @param source l'Objet Symphony source
	 * @param target l'Objet Symphony destination
	 */
	private void bind(final SymphonyObject source, final SymphonyObject target) {
		
		BrokerReference bkRef = null ;
		
		// TODO On valide la connexion entre source et target (une BrokerReference doit exister)
		for (Class<?> singleInterface : source.getClass().getInterfaces()) {
			if (!(singleInterface.getName().matches("common.process.ProcessObject") || 
					singleInterface.getName().matches("common.entity.EntityObject") ||
					singleInterface.getName().matches("interaction.common.Displayable"))) {
				bkRef = referenceTable.get(singleInterface.getName());
				break ;
			}
		}
		
		BrokerConnection connection = connectionTable.get(source);
		
		// Si la table de connexions de possede pas deja de reference,
		// on prepare une nouvelle connexion
		if (connection == null) {
			
			connection = new BrokerConnection() ;
			connectionTable.put(source, connection);
		} 
	
		for (Class<?> singleClass : bkRef.destinations) {
			for (Class<?> targetInterface : target.getClass().getInterfaces()) {
				if (targetInterface.equals(singleClass)) {
					connection.getTranslation().addDestination((Class<SymphonyObject>) targetInterface, target) ;
					break ;
				}
			}
		}	
	
		
		logger.fine("connectionTable : nouveau lien entre " + source + " et " + target) ;
	}

	public void setUnitTesting(boolean trueFalse) {
		isUnitTesting = trueFalse ;
	}
	
}
