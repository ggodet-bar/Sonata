package org.sonata.framework.control.invoker;

import org.sonata.framework.common.ReferenceType;

public class ReferenceElement {
	Class<?> klazz ;
	ReferenceType type ;
	
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
}
