package sonata.test.unit.abstractentityfactory;

import java.util.Properties;

import org.sonata.framework.common.entity.EntityObject;

public class SampleObject2Impl implements SampleObject2, EntityObject {
	String address ;
	
	public SampleObject2Impl(Properties prop) {
		if (prop != null) {
			address = prop.getProperty("address") ;
		}
	}
	
	@Override
	public String address() {
		return address ;
	}
}
