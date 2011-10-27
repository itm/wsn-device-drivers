package de.uniluebeck.itm.wsn.drivers.sunspot;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;

public class SunspotIsAliveOperationRunnable implements ResetOperation {

    private String macAddress;

    private ant_project p;


    public SunspotIsAliveOperationRunnable(String macAddress, String SunspotSDKPath, String commandPort, String tmpDirectory) {
        this.macAddress = macAddress;
        p = new ant_project(SunspotSDKPath,commandPort,tmpDirectory);

    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        p.info(macAddress );
        return null;
    }
}
