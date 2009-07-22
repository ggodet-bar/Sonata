package sonata.test.sampleobject;

import java.util.Properties;

import org.sonata.framework.common.entity.EntityObject;

public class SampleObjectImpl implements SampleObject, EntityObject {

	private String username ;
	
	public SampleObjectImpl(Properties prop) {
		if (prop != null) {
			username = prop.getProperty("username") ;
		}
	}
	
	public String username() {
		return username ;
	}

	@Override
	public void triggeringCall() {
		
	}
}
