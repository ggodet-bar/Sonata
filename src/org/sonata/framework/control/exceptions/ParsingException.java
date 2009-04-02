package org.sonata.framework.control.exceptions;

public class ParsingException extends Exception {

	private static final long serialVersionUID = 7247441252376161406L;
	private transient final String errString ;
	
	public ParsingException() {
		super() ;
		errString = "unknown" ;
	}
	
	public ParsingException(final String err) {
		super(err) ;
		errString = err ;
	}
	
	public String getMessage() {
		return errString ;
	}
}
