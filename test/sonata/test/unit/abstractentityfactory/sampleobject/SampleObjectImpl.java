package sonata.test.unit.abstractentityfactory.sampleobject;

import java.awt.Dimension;

import org.sonata.framework.common.entity.EntityObject;

public class SampleObjectImpl implements SampleObject, EntityObject {
	private String username ;
	private int age ;
	private boolean isMale ;
	private Dimension flatDimensions ;
	
	public String getUsername() {
		return username ;
	}
	
	public void setUsername(String username) {
		this.username = username ;
	}

	@Override
	public void triggeringCall() {
		
	}

	@Override
	public int getAge() {
		return age ;
	}

	@Override
	public void setAge(int age) {
		this.age = age ;
	}

	@Override
	public boolean isMale() {
		return isMale ;
	}

	@Override
	public void setMale(boolean isMale) {
		this.isMale = isMale ;
	}

	@Override
	public Dimension getFlatDimensions() {
		return flatDimensions ;
	}

	@Override
	public void setFlatDimensions(short width, short length) {
		flatDimensions = new Dimension(width, length) ;
	}

}

