package org.sonata.framework.control.exceptions;

public class InvalidSOConnection extends Exception {

	private static final long serialVersionUID = -5411628909070878713L;
	private transient final String errorString ;
	
	public InvalidSOConnection() {
		super() ;
		errorString = "unknown" ;
	}
	
	public InvalidSOConnection(final String err) {
		super(err) ;
		errorString = err ;
	}
	
	public String getMessage() {
		return errorString ;
	}
}
