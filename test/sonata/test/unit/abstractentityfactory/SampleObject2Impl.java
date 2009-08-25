package sonata.test.unit.abstractentityfactory;

import org.sonata.framework.common.entity.EntityObject;

public class SampleObject2Impl implements SampleObject2, EntityObject {
	String address ;
	
	@Override
	public String getAddress() {
		return address ;
	}
	
	@Override
	public void setAddress(String address) {
		this.address = address ;
	}
}
