package org.sonata.framework.control.exceptions;

public class RequestOverlapException extends Exception {

	private static final long serialVersionUID = -2846337349361501544L;
	private transient final String errorString;
	
	public RequestOverlapException() {
		super();
		errorString = "unknown";
	}
	
	public RequestOverlapException(final String err) {
		super(err);
		errorString = err ;
	}
	
	public String getMessage() {
		return errorString ;
	}
}
