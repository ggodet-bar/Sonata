package org.sonata.framework.control.invoker;

import java.util.List;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

public class BrokerReference {

	public Class<SymphonyObject> source ;
	public ReferenceType sourceType ;
	public Class<ProcessObject> proxy ;
	public List<Class<SymphonyObject>> destinations ;
	public Class<ConnectionTranslation> translation ;
	
	public BrokerReference(Class<SymphonyObject> source, ReferenceType refSource, List<Class<SymphonyObject>> destinations, Class<ProcessObject> proxy, Class<ConnectionTranslation> translation) {
		this.source = source ;
		this.sourceType = refSource ;
		this.destinations = destinations ;
		this.proxy = proxy ;
		this.translation = translation ;
	}
}
