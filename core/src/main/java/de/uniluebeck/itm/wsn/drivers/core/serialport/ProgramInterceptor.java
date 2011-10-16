package de.uniluebeck.itm.wsn.drivers.core.serialport;

import gnu.io.SerialPort;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.exception.EnterProgramModeException;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;

/**
 * Interceptor that allows the usage of the Program annotation.
 * 
 * @author Malte Legenhausen
 */
public class ProgramInterceptor implements MethodInterceptor {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProgramInterceptor.class);
	
	/**
	 * Sleep time between setting DTR and RTS.
	 */
	private static final int SLEEP = 200;
	
	private final SerialPortConnection connection;
	
	private boolean programMode = false;
	
	public ProgramInterceptor(SerialPortConnection connection) {
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
			enterProgramMode();
			try {
				result = invocation.proceed();
			} finally {
				leaveProgramMode();
			}
		} finally {
			connection.release();
		}
		return result;
	}
	
	private void enterProgramMode() throws Exception {
		LOG.trace("Entering program mode...");
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		final SerialPort serialPort = connection.getSerialPort();
		try {
			serialPort.setDTR(true);
			Thread.sleep(SLEEP);
			serialPort.setRTS(true);
			Thread.sleep(SLEEP);
			serialPort.setDTR(false);
			Thread.sleep(SLEEP);
			serialPort.setRTS(false);
		} catch (final InterruptedException e) {
			LOG.error("Unable to enter program mode.", e);
			throw new EnterProgramModeException("Unable to enter program mode.");
		}
		connection.clear();
		programMode = true;
		LOG.trace("Program mode entered");
	}
	
	private void leaveProgramMode() throws Exception {
		LOG.trace("Leaving program mode...");
		connection.clear();
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		programMode = false;
		LOG.trace("Program mode left");
	}
}
