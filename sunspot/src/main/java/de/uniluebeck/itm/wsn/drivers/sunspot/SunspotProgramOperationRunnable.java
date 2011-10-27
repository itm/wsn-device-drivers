package de.uniluebeck.itm.wsn.drivers.sunspot;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class SunspotProgramOperationRunnable implements ProgramOperation {

    private String macAddress;
    private byte[] image;
    private ant_project p;

    public SunspotProgramOperationRunnable(String macAddress,String SunspotSDKPath, String commandPort, String tempDir,  byte[] jar) {
        this.macAddress = macAddress;
        this.image = jar;
        p = new ant_project(SunspotSDKPath,commandPort,tempDir);
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        System.out.println("SUNSPOT node flash node>>>>" + "  " + this.macAddress);
        p.flash_node(macAddress, image, true);
        return null;
    }

    @Override
    public void setBinaryImage(byte[] data) {
        this.image = data;
    }
}
