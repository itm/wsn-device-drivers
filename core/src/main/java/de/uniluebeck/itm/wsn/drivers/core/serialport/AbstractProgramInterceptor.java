package de.uniluebeck.itm.wsn.drivers.core.serialport;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * Abstract class for using the Program annotation in specific drivers.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractProgramInterceptor implements MethodInterceptor {
	
	private final SerialPortConnection connection;
	
	private boolean programMode = false;
	
	public AbstractProgramInterceptor(SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (programMode) {
			return invocation.proceed();
		}
		
		Object result = null;		
		connection.prepare();
		try {
			enterProgramMode(connection);
			programMode = true;
			try {
				result = invocation.proceed();
			} finally {
				leaveProgramMode(connection);
				programMode = false;
			}
		} finally {
			connection.release();
		}
		return result;
	}
	
	public abstract void enterProgramMode(SerialPortConnection aConnection) throws Exception;
	
	public abstract void leaveProgramMode(SerialPortConnection aConnection) throws Exception;
}
