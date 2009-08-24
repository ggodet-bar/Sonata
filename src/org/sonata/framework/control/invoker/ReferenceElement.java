package org.sonata.framework.control.invoker;

import org.sonata.framework.common.ReferenceType;

public class ReferenceElement {
	Class<?> klazz ;
	ReferenceType type ;
	
	public ReferenceElement(Class<?> class1, ReferenceType type) {
		this.klazz = class1 ;
		this.type = type ;
	}
}
