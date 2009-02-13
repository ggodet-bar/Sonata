package org.sonata.framework.control.exceptions;

public class IllegalCallException extends Exception {

	private static final long serialVersionUID = 825155657385131941L;
	private String errorString ;
	
	public IllegalCallException() {
		super() ;
		errorString = "unknown" ;
	}
	
	public IllegalCallException(final String err) {
		super(err) ;
		errorString = err ;
	}
	
	public String getMessage() {
		return errorString ;
	}
}
