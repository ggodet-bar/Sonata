package sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.common.entity.EntityObjectServices;
import org.sonata.framework.common.entity.EntityObject;

public class SampleObjectWithTechnicalComponentImpl implements
		SampleObjectWithTechnicalComponent, EntityObject {

	private TechnicalComponent techComp ;
	
	public SampleObjectWithTechnicalComponentImpl() {
		techComp = ((EntityObjectServices)this).getTechnicalComponentInstance(TechnicalInterface.class) ;
	}
	
	@Override
	public TechnicalInterface exposeTechnicalComponent() {
		return (TechnicalInterface) techComp;
	}

}
