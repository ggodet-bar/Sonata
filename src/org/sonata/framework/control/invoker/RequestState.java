package org.sonata.framework.control.invoker;

public enum RequestState {
	/**
	 * The request has just been created and is being constructed
	 */
	INCOMPLETE,
	
	/**
	 * The request has been sent to the Invoker, and a response is expected
	 */
	SENT,
	
	/**
	 * A response has been received, and the request can be considered as resolved
	 */
	RESPONSE_RECEIVED
}
