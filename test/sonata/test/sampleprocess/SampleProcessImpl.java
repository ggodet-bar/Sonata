package sonata.test.sampleprocess;

import org.sonata.framework.common.TechnicalComponent;
import org.sonata.framework.common.process.ProcessObject;
import org.sonata.framework.common.process.ProcessObjectServices;

public class SampleProcessImpl implements SampleProcess, ProcessObject {

	private TechnicalInterface theComponent ;
	
	public SampleProcessImpl() {
		theComponent = (TechnicalInterface) ((ProcessObjectServices)this).getTechnicalComponentInstance(TechnicalInterface.class) ;
	}
	
	public TechnicalComponent exposeTechnicalComponent() {
		return theComponent ;
	}
}
