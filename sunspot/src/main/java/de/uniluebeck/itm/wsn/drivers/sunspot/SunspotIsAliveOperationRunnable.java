package de.uniluebeck.itm.wsn.drivers.sunspot;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;

public class SunspotIsAliveOperationRunnable implements ResetOperation {

    private String macAddress;

    public SunspotIsAliveOperationRunnable(String macAddress, String SunspotSDKPath, String commandPort, String tmpDirectory) {
        this.macAddress = macAddress;
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        return null;
    }
}
