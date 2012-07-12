package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MockOperationInterceptor implements MethodInterceptor {

	@Inject
	private MockDevice device;

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		try {
			device.acquireLockOnDevice();
			return invocation.proceed();
		} finally {
			device.releaseLockOnDevice();
		}
	}
}
