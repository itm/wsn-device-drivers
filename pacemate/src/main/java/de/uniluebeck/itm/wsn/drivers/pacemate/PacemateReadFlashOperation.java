package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;

import javax.annotation.Nullable;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {

	private final PacemateHelper helper;

	@Inject
	public PacemateReadFlashOperation(final TimeLimiter timeLimiter,
									  final PacemateHelper helper,
									  @Assisted("address") int address,
									  @Assisted("length") int length,
									  @Assisted final long timeoutMillis,
									  @Assisted @Nullable final OperationListener<byte[]> operationCallback) {
		super(timeLimiter, address, length, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected byte[] callInternal() throws Exception {
		// Send flash program request
		helper.sendBootLoaderMessage(Messages.flashReadRequestMessage(getAddress(), getLength()));
		progress(0.5f);
		return helper.receiveBootLoaderReplyReadData();
	}
}
