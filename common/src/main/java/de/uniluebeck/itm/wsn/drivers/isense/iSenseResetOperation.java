package de.uniluebeck.itm.wsn.drivers.isense;

import gnu.io.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;

public class iSenseResetOperation implements ResetOperation {
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(iSenseResetOperation.class);
	
	private final SerialPortConnection connection;

	@Inject
	public iSenseResetOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}

	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.debug("Resetting device...");
		SerialPort serialPort = connection.getSerialPort();
		serialPort.setDTR(true);
		progressManager.worked(0.5f);
		Thread.sleep(200);
		serialPort.setDTR(false);
		progressManager.worked(0.5f);
		log.debug("Device resetted");
		return null;
	}

}
