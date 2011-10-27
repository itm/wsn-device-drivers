package de.uniluebeck.itm.wsn.drivers.sunspot;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;

public class SunspotChipTypeOperationRunnable implements ResetOperation {

    private String macAddress;

    private ant_project p;

    public SunspotChipTypeOperationRunnable(String macAddress, String SunspotSDKPath, String commandPort, String tempDir) {
        this.macAddress = macAddress;
        p = new ant_project(SunspotSDKPath,commandPort,tempDir);

    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        System.out.println("SUNSPOT node reset node>>>>" + "  " + this.macAddress);
        p.resetNode(macAddress);
        return null;
    }
}
