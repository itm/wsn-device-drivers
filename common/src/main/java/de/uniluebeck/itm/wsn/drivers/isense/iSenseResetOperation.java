package de.uniluebeck.itm.wsn.drivers.isense;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import gnu.io.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class iSenseResetOperation extends TimeLimitedOperation<Void> implements ResetOperation {

	private static final Logger log = LoggerFactory.getLogger(iSenseResetOperation.class);

	private final SerialPortConnection connection;

	@Inject
	public iSenseResetOperation(final TimeLimiter timeLimiter,
								final SerialPortConnection connection,
								@Assisted final long timeoutMillis,
								@Assisted @Nullable final OperationListener<Void> operationListener) {
		super(timeLimiter, timeoutMillis, operationListener);
		this.connection = connection;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {
		log.debug("Resetting device...");
		SerialPort serialPort = connection.getSerialPort();
		serialPort.setDTR(true);
		progress(0.5f);
		Thread.sleep(200);
		serialPort.setDTR(false);
		progress(0.5f);
		log.debug("Device reset");
		return null;
	}
}
