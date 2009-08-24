package sonata.test.unit.invoker;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.common.process.ProcessObject;

public class SampleObjectTranslation extends ConnectionTranslation {

	public SampleObjectTranslation(SymphonyObject source, ProcessObject proxy) {
		super(source, proxy) ;
	}
	
	public void translateCall() {
		System.out.println("translateCall") ;
	}
	
	public void callWithParameters(int anInteger, String aString) {
		System.out.println("CallWithParameters") ;
	}
	
	public void callWithNonUniformParameters(Integer anInt, int anotherInt, Long aLong, Boolean aBool, boolean anotherBool, String aString) {
		System.out.println("This should not be displayed!") ;
	}
	
	public void throwException() throws Exception {
		throw new Exception("Sample exception") ;
	}
}