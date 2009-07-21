package sonata.test.translation;

import org.sonata.framework.common.ConnectionTranslation;

import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.process.ProcessObject;

import sonata.test.sampleobject2.SampleObject2;

public class SampleObjectTranslation extends ConnectionTranslation {

	public SampleObjectTranslation(SymphonyObject source, ProcessObject proxy) {
		super(source, proxy) ;
	}
	
	public void translateCall() {
		AbstractEntityFactory.instance.createEntity(SampleObject2.class) ;
	}
}
