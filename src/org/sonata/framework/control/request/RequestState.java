package org.sonata.framework.control.request;

public enum RequestState {
	INCOMPLETE,	// La requête est en cours de construction
	SENT,	// La requête a été envoyée, une réponse est en cours d'attente
	RESPONSE_RECEIVED	// La réponse à la requête a été reçue, il est donc
						// possible d'en créer une nouvelle
}
