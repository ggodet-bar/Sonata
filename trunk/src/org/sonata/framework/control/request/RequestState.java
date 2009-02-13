package org.sonata.framework.control.request;

public enum RequestState {
	INCOMPLETE,	// La requ�te est en cours de construction
	SENT,	// La requ�te a �t� envoy�e, une r�ponse est en cours d'attente
	RESPONSE_RECEIVED	// La r�ponse � la requ�te a �t� re�ue, il est donc
						// possible d'en cr�er une nouvelle
}
