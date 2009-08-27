package org.sonata.framework.control.invoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.ReferenceType;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

public class BrokerReference {

	private ReferenceElement source ;
	private Class<ProcessObject> proxy ;
	private List<ReferenceElement> destinations ;
	private Class<? extends ConnectionTranslation> translation ;
	
	public BrokerReference() {
		destinations = new ArrayList<ReferenceElement>() ;
	}
	
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) return false ;
		
		BrokerReference test = (BrokerReference) o ;
		return (test.source.equals(source) &&
				(proxy == null || test.proxy.equals(proxy))	&&
				test.destinations.containsAll(destinations)	&&
				test.translation.equals(translation)
				) ;
	}
	
	public int hashCode() {
		int hash = 7 ;
		hash = hash * 31 + source.getReferenceClass().getName().hashCode() ;
		hash = hash * 31 + destinations.size() ;
		hash = hash * 31 + destinations.get(0).getReferenceClass().getName().hashCode() ;
		hash = hash * 31 + translation.getName().hashCode() ;
		return hash ;
	}
	
	public void setSource(Class<SymphonyObject> klazz, ReferenceType type) {
		ReferenceElement reference = new ReferenceElement(klazz, type) ;
		this.source = isReferenceElementValid(reference) ? reference : null ;
		if (klazz != null && type != null) {
			this.source = new ReferenceElement(klazz, type) ;
		}
	}
	
	public void setSource(ReferenceElement element) {
		this.source = isReferenceElementValid(element) ? element : null ;
	}
	
	public ReferenceElement getSource() {
		return this.source ;
	}
	
	public String getIndex() {
		return this.source.getReferenceClass().getName() ;
	}
	
	public void setProxy(Class<ProcessObject> proxy) {
		this.proxy = proxy ;
	}
	
	public Class<ProcessObject> getProxy() {
		return this.proxy ;
	}
	
	public void addDestination(ReferenceElement element) {
		if (isReferenceElementValid(element)) {
			destinations.add(element) ;
		}
	}
	
	public List<ReferenceElement> getDestinations() {
		return Collections.unmodifiableList(destinations) ;
	}
	
	public void setTranslation(Class<? extends ConnectionTranslation> translation) {
		this.translation = translation ;
	}
	
	public Class<? extends ConnectionTranslation> getTranslation() {
		return this.translation ;
	}
	
	private boolean isReferenceElementValid(ReferenceElement element) {
		return element.getReferenceClass() != null && element.getReferenceType() != null ;
	}
}