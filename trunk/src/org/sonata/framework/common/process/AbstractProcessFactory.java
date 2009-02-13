package org.sonata.framework.common.process;

import org.sonata.framework.common.AbstractFactory;

public abstract class AbstractProcessFactory extends AbstractFactory {

	protected ProcessObject objectInstance ;
	
	protected AbstractProcessFactory() {
		super() ;
	}
	
	public ProcessObject getProcessus() {
		return objectInstance ;
	}
	
	public void clearProcessus() {
		objectInstance = null ;
	}

	public abstract Object creerProcessus() ;
}
