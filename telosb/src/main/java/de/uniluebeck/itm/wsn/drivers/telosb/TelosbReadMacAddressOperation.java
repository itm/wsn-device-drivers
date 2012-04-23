package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;

import javax.annotation.Nullable;

public class TelosbReadMacAddressOperation extends AbstractUnsupportedOperation<MacAddress> implements
		ReadMacAddressOperation {

	@Inject
	public TelosbReadMacAddressOperation(final TimeLimiter timeLimiter,
										 @Assisted final long timeoutMillis,
										 @Assisted @Nullable
										 final OperationListener<MacAddress> macAddressOperationListener) {
		super(timeLimiter, timeoutMillis, macAddressOperationListener);
	}
}
