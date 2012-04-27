package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.spot.client.ui.SunspotCommandUI;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class SunspotResetOperation extends TimeLimitedOperation<Void> implements ResetOperation {

	private static final Logger log = LoggerFactory.getLogger(SunspotResetOperation.class);

	private final String macAddress;

	private final String sysBinPath;

	private final String libFilePath;

	private final String keyStorePath;

	private final String port;

	private final String iport;

	@Inject
	public SunspotResetOperation(final TimeLimiter timeLimiter,
								 @Assisted final long timeoutMillis,
								 @Assisted @Nullable final OperationListener<Void> operationListener,
								 final String iport, final String macAddress, final String sysBinPath,
								 final String libFilePath, final String keyStorePath, final String port) {

		super(timeLimiter, timeoutMillis, operationListener);
		this.iport = iport;
		this.macAddress = macAddress;
		this.sysBinPath = sysBinPath;
		this.libFilePath = libFilePath;
		this.keyStorePath = keyStorePath;
		this.port = port;
	}

	@Override
	protected Void callInternal() throws Exception {

		final SunspotCommandUI ss = new SunspotCommandUI();
		try {
			log.debug("RESET NODE:" + this.macAddress);
			String[] args = new String[7];
			args[0] = this.sysBinPath;
			args[1] = this.libFilePath;
			args[2] = this.keyStorePath;
			args[3] = this.port;
			args[4] = this.iport;
			args[5] = "-remote.address=" + this.macAddress;
			args[6] = "-scriptString=reboot:quit";
			ss.initialize(args);
		} catch (Exception e) {
			log.error("RESET ERROR:" + this.macAddress + ": " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		log.debug("RESET OK:" + this.macAddress);

		return null;
	}
}
