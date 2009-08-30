package sonata.test.integration;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.sonata.framework.common.entity.AbstractEntityFactory;
import org.sonata.framework.common.entity.EntityObject;
import org.sonata.framework.common.entity.EntityObjectServices;
import org.sonata.framework.control.invoker.Invoker;
import org.sonata.framework.control.invoker.Request;

import sonata.test.unit.abstractentityfactory.sampleobject.SampleObject;

@Aspect
public class SampleObjectAspect {

	
	Request req ;
	
	@Pointcut("execution(public * SampleObject.*(..))")
	void SampleObjectCalls() {}

	@AfterReturning(pointcut = "execution(Object AbstractEntityFactory.createEntity(Class<?>)) && args(theClass)", returning ="newObject")
	public void after(Class<?> theClass, Object newObject) {
		if (newObject instanceof SampleObject) {
			System.out.println("There was a call for creating an entity " + ((EntityObjectServices)newObject).getID()) ;
		}
	}
	
	@After("SampleObjectCalls() && execution(void triggeringCall()) && target(target)")
	public void after(SampleObject target)  {
		try{
			req = Invoker.getInstance().createRequest((EntityObject)target, "translateCall", null);

			Invoker.getInstance().sendRequest();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	
}