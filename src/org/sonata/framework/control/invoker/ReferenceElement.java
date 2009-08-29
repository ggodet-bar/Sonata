package org.sonata.framework.control.invoker;

import org.sonata.framework.common.ReferenceType;

/**
 * Simple class that describes basic elements of a <code>BrokerReference</code>
 * instance, that is, a {<code>Class<?>, ReferenceType</code>} pair.
 * <code>ReferenceElement</code> instances are essentially used for 
 * characterizing connections between Business and Interactional Objects.
 * 
 * @author Guillaume Godet-Bar
 *
 * @see org.sonata.framework.control.invoker.BrokerReference
 */
public class ReferenceElement {
	private Class<?> klazz ;
	private ReferenceType type ;
	
	public ReferenceElement(Class<?> class1, ReferenceType type) {
		this.klazz = class1 ;
		this.type = type ;
	}
	
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) return false ;
		ReferenceElement test = (ReferenceElement)o ;
		return (test.klazz.equals(klazz) &&
				test.type.equals(type)
				) ;
	}
	
	public Class<?> getReferenceClass() {
		return klazz;
	}

	public void setReferenceClass(Class<?> klazz) {
		this.klazz = klazz;
	}

	public ReferenceType getReferenceType() {
		return type;
	}

	public void setReferenceType(ReferenceType type) {
		this.type = type;
	}
}
