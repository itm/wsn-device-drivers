package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.operation.IsNodeAliveOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class SimpleIsNodeAliveOperation implements IsNodeAliveOperation {

	/**
	 * Logger for this class.
	 */

	private final Device device;

	@Inject
	public SimpleIsNodeAliveOperation(Device device) {
        this.device = device;
    }


    @Override
    public Boolean run(ProgressManager progressManager, OperationContext context) throws Exception {
       return device.isConnected();
    }
}
