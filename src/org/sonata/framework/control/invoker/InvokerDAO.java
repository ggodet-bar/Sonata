package org.sonata.framework.control.invoker;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class used by the <code>Invoker</code> for loading 
 * <code>BrokerReference</code> instances from a data source.
 * The <code>BrokerReference</code> parsed from the data source must
 * be stored into the <code>theReferences</code> protected variable
 * in order for the Invoker to access this data with a call to the 
 * <code>getBrokerReferences()</code> method.
 * 
 * @see org.sonata.framework.control.invoker.Invoker
 * 
 * @author Guillaume Godet-Bar
 *
 */
public abstract class InvokerDAO {

	protected List<BrokerReference>	theReferences ;
	
	public InvokerDAO() {
		theReferences = new LinkedList<BrokerReference>() ;
	}
	
	public List<BrokerReference> getBrokerReferences() {
		return theReferences ;
	}
}
