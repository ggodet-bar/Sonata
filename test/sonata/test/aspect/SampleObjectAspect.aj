package sonata.test.aspect;

import org.sonata.framework.common.SymphonyObject;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.control.request.Request;

import sonata.test.sampleobject.SampleObject;


public aspect SampleObjectAspect {

	
	Request req ;
	
	pointcut SampleObjectCalls() : execution(public * SampleObject.*(..)) ;
		


	after(SampleObject target) : SampleObjectCalls() && execution(void triggeringCall()) && target(target) {
		try{
			req = Invoker.instance.createRequest((SymphonyObject)target, "translateCall", null);

			Invoker.instance.sendRequest();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	
}
