package sonata.test.integration;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

import sonata.test.unit.abstractentityfactory.sampleobject2.SampleObject2;
import sonata.test.unit.abstractentityfactory.sampleobjectwithtechnicalcomponent.SampleObjectWithTechnicalComponent;

public class SampleObjectTranslation extends ConnectionTranslation {
	
	public SampleObjectTranslation(SymphonyObject source, ProcessObject proxy) {
		super(source, proxy);
	}

	public void translateCall() {
		SampleObject2 o = (SampleObject2) BasicSonataOperations.theFactory.createEntity(SampleObject2.class) ;
		BasicSonataOperations.address = o.getAddress() ;
	}
	
	public void technicalComponentCall() {
		SampleObjectWithTechnicalComponent o = (SampleObjectWithTechnicalComponent) BasicSonataOperations.theFactory.createEntity(SampleObjectWithTechnicalComponent.class) ;
		BasicSonataOperations.technicalComponent = o.exposeTechnicalComponent() ;
	}
	
	public void bothCall() {
		SampleObject2 o1 = (SampleObject2) BasicSonataOperations.theFactory.createEntity(SampleObject2.class) ;
		SampleObjectWithTechnicalComponent o2 = (SampleObjectWithTechnicalComponent) BasicSonataOperations.theFactory.createEntity(SampleObjectWithTechnicalComponent.class) ;
		
		BasicSonataOperations.address = o1.getAddress() ;
		BasicSonataOperations.technicalComponent = o2.exposeTechnicalComponent() ;
	}
	
}