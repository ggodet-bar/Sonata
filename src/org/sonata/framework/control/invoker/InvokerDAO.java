package org.sonata.framework.control.invoker;

import java.util.LinkedList;
import java.util.List;

public abstract class InvokerDAO {

	protected List<BrokerReference>	theReferences ;
	
	public InvokerDAO() {
		theReferences = new LinkedList<BrokerReference>() ;
	}
	
	public List<BrokerReference> getBrokerReferences() {
		return theReferences ;
	}
}
