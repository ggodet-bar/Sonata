package sonata.test.sampleobject2;

import java.util.Properties;

import org.sonata.framework.common.entity.EntityObject;

public class SampleObject2Impl implements EntityObject, SampleObject2 {

	String address ;
	
	public SampleObject2Impl(Properties prop) {
		address = prop.getProperty("address") ;
		
	}
	
	@Override
	public String address() {
		return address ;
	}

}
