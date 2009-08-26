package sonata.test.unit.abstractentityfactory.sampleobject;

import java.awt.Dimension;

public interface SampleObject {
	String getUsername();
	
	void setUsername(String username) ;

	void triggeringCall() ;

	int getAge();
	
	void setAge(int age) ;

	void setMale(boolean isMale);
	
	boolean isMale() ;
	
	void setFlatDimensions(short width, short length) ;
	
	Dimension getFlatDimensions() ;
	
}