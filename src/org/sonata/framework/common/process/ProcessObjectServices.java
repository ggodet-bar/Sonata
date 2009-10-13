package org.sonata.framework.common.process;

import org.sonata.framework.common.TechnicalComponent;

public interface ProcessObjectServices {
	public void setTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz, TechnicalComponent comp) ;
	public TechnicalComponent getTechnicalComponentInstance(Class<? extends TechnicalComponent> klazz) ;
}
