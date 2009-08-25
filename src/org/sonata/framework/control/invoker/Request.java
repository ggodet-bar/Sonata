package org.sonata.framework.control.invoker;

import static org.sonata.framework.control.invoker.RequestState.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

public class Request{

	private final SymphonyObject associatedOS ;
	
	private final ProcessObject proxy ;
	
	private final String operationName ;
	
	private final Queue<Object> args ;
	
	private final Queue<Class<?>> argTypes ;
	
	private RequestState state ;
	
	private Object returnValue ;
	
	private boolean hasReturnValue ;
	
	Request(SymphonyObject OS, String operationName, ProcessObject proxy) {
		this.associatedOS = OS ;
		this.operationName = operationName ;
		this.proxy = proxy ;
		this.args = new LinkedList<Object>() ;
		this.argTypes = new LinkedList<Class<?>>() ;
		state = INCOMPLETE ;
	}
	
	// NB : Impl�mentation de la liste des arguments comme une file
	public void pushParameter(final Object param) {
		args.offer(param) ;
		argTypes.offer(param.getClass()) ;
	}
	
	public Object popParameter() {
		Object tmp = null ;
		if (!args.isEmpty()) {
			tmp = args.remove() ;
			argTypes.remove() ; // Pour garder la synchro arg/argType
		}
		return tmp ;
	}
	
	public SymphonyObject getAssociatedSymphonyObject() {
		return associatedOS ;
	}
	
	public boolean isArgListEmpty() {
		return args.isEmpty() ;
	}

	public RequestState getRequestState() {
		return state ;
	}
	
	public ProcessObject getProxy() {
		return proxy ;
	}

	
	public void nextState() {
		switch (state) {
			case INCOMPLETE :
				state = SENT ;
			break ;
			
			case SENT :
				state = RESPONSE_RECEIVED ;
			break ;
			
			case RESPONSE_RECEIVED :
			break ;
		}
		
	}

	public String getOpName() {
		return operationName ;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getParamArray() {
		if (args == null) return null ;
		return (List<Object>)((LinkedList)args).clone() ;
	}


	public Class<?>[] getParamTypeArray() {
		Class<?> [] objTypeList = null ;
		if (argTypes != null) {
			objTypeList = new Class[args.size()] ;
			int index = 0 ;
			for (Object obj : args)
			{
				objTypeList[index] = obj.getClass() ;
				index++ ;
			}
		}
		
		return objTypeList ;
	}
	
	public Object getReturnValue() {
		return returnValue ;
	}
	
	public void setReturnValue(Object value) {
		returnValue = value ;
	}
	
	public boolean hasReturnedValue() {
		return hasReturnValue ;
	}
	
	public void setHasReturnValue(boolean bool) {
		hasReturnValue = bool ;
	}
}
