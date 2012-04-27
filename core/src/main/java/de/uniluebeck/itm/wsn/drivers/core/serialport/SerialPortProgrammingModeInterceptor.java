package de.uniluebeck.itm.wsn.drivers.core.serialport;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.exception.EnterProgrammingModeException;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;
import gnu.io.SerialPort;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor that allows the usage of the Program annotation.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class SerialPortProgrammingModeInterceptor implements MethodInterceptor {

	private static final Logger log = LoggerFactory.getLogger(SerialPortProgrammingModeInterceptor.class);

	/**
	 * Sleep time between setting DTR and RTS.
	 */
	private static final int SLEEP_DTR_DTS = 200;

	@Inject
	private SerialPortDevice device;

	private boolean alreadyInProgrammingMode;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		log.trace("Intercepting programming mode (alreadyInProgrammingMode={})", alreadyInProgrammingMode);

		if (alreadyInProgrammingMode) {
			return invocation.proceed();
		}

		Object result = null;

		device.acquireLockOnDevice();
		try {
			enterProgrammingMode();
			alreadyInProgrammingMode = true;
			try {
				result = invocation.proceed();
			} finally {
				leaveProgrammingMode();
				alreadyInProgrammingMode = false;
			}
		} finally {
			device.releaseLockOnDeviceStreams();
		}
		return result;
	}

	public void enterProgrammingMode() throws Exception {

		log.trace("Entering programming mode...");
		((SerialPortConnection) device.getConnection()).setSerialPortMode(SerialPortMode.PROGRAM);

		final SerialPort serialPort = ((SerialPortConnection) device.getConnection()).getSerialPort();
		try {

			serialPort.setDTR(true);
			Thread.sleep(SLEEP_DTR_DTS);
			serialPort.setRTS(true);
			Thread.sleep(SLEEP_DTR_DTS);
			serialPort.setDTR(false);
			Thread.sleep(SLEEP_DTR_DTS);
			serialPort.setRTS(false);

		} catch (final InterruptedException e) {
			log.error("Unable to enter programming mode.", e);
			throw new EnterProgrammingModeException("Unable to enter programming mode.");
		}

		device.getConnection().clear();
		log.trace("Programming mode entered");

	}

	public void leaveProgrammingMode() throws Exception {
		log.trace("Leaving programming mode...");
		device.getConnection().clear();
		((SerialPortConnection) device.getConnection()).setSerialPortMode(SerialPortMode.NORMAL);
		log.trace("Programming mode left");
	}
}
