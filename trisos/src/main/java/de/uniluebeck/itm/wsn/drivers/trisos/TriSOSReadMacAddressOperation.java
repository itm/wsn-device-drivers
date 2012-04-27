package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;

import javax.annotation.Nullable;

public class TriSOSReadMacAddressOperation extends AbstractUnsupportedOperation<MacAddress> implements
		ReadMacAddressOperation {

	@Inject
	public TriSOSReadMacAddressOperation(final TimeLimiter timeLimiter, final long timeoutMillis,
										 @Nullable final OperationListener<MacAddress> macAddressOperationListener) {
		super(timeLimiter, timeoutMillis, macAddressOperationListener);
	}
}
