package org.sonata.framework.control.request;

import java.util.List;

import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

public interface Request {

	void pushParameter(Object param) ;
	
	Object popParameter() ;
	
	RequestState getRequestState() ;
	
	String getOpName() ;
	
	Class<?>[] getParamTypeArray() ;
	
	List<Object> getParamArray() ;
	
	SymphonyObject getAssociatedSymphonyObject() ;
	
	void nextState() ;
	
	Object getReturnValue() ;
	
	ProcessObject getProxy() ;
	
	void setReturnValue(Object value) ;
	
	boolean hasReturnedValue() ;
	
	void setHasReturnValue(boolean bool) ;
}
