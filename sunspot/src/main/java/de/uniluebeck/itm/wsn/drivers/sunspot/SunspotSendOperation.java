package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.spot.client.ui.SunspotCommandUI;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

public class SunspotSendOperation extends TimeLimitedOperation<Void> implements ProgramOperation {

	private static final Logger log = LoggerFactory.getLogger(SunspotSendOperation.class);

	private final byte[] message;

	private final String macAddress;

	private final int commandRadiogramPort;

	@Inject
	public SunspotSendOperation(final TimeLimiter timeLimiter,
								@Assisted final long timeoutMillis,
								@Assisted @Nullable final OperationListener<Void> voidOperationListener,
								final int commandRadiogramPort, final byte[] message, final String macAddress) {

		super(timeLimiter, timeoutMillis, voidOperationListener);
		this.commandRadiogramPort = commandRadiogramPort;
		this.message = message;
		this.macAddress = macAddress;
	}

	@Override
	protected Void callInternal() throws Exception {
		final SunspotCommandUI ss = new SunspotCommandUI();
		try {
			this.start();
			log.debug("Send msg NODE:" + this.macAddress);
		} catch (Exception e) {
			log.error("Send msg ERROR:" + this.macAddress + ": " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		return null;
	}

	private void start() throws Exception {

		RadiogramConnection dgConnection;
		Datagram dg;

		dgConnection = (RadiogramConnection) Connector.open(
				"radiogram://" + this.macAddress + ":" + this.commandRadiogramPort
		);
		dgConnection.setMaxBroadcastHops(1);
		dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
		dg.reset();
		dg.write(message);
		dgConnection.send(dg);
		System.out.println("Broadcast is going through");
	}
}


