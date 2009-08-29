package sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent;

import org.sonata.framework.common.entity.EntityObjectServices;
import org.sonata.framework.common.entity.EntityObject;

public class SampleObjectWithTechnicalComponentImpl implements
		SampleObjectWithTechnicalComponent, EntityObject {

	@Override
	public TechnicalInterface exposeTechnicalComponent() {
		return (TechnicalInterface) ((EntityObjectServices)this).getTechnicalComponentInstance(TechnicalInterface.class) ;
	}

}
