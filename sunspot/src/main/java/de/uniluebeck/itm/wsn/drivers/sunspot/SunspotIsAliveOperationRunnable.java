package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.sun.spot.client.ui.SunspotCommandUI;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SunspotIsAliveOperationRunnable implements ResetOperation {
    private static final Logger log = LoggerFactory.getLogger(SunspotIsAliveOperationRunnable.class);
    private String macAddress;
    private String sysBinPath;
    private String libFilePath;
    private String keyStrorePath;
    private String port;
    private String iport;


    public SunspotIsAliveOperationRunnable(String macAddress, String sysBinPath, String libFilePath, String keyStrorePath, String port, String iport) {
        this.macAddress = macAddress;
        this.sysBinPath = sysBinPath;
        this.libFilePath = libFilePath;
        this.keyStrorePath = keyStrorePath;
        this.port = port;
        this.iport = iport;
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        log.debug("SUNSPOT IsNodeALive node>>>>" + "  " + this.macAddress);
        final SunspotCommandUI ss = new SunspotCommandUI();
        try {
            log.debug("IsNodeAlive NODE:" + this.macAddress);
            String[] args = new String[7];
            args[0] = this.sysBinPath;
            args[1] = this.libFilePath;
            args[2] = this.keyStrorePath;
            args[3] = this.port;
            args[4] = this.iport;
            args[5] = "-remote.address=" + this.macAddress;
            args[6] = "-scriptString=info:quit";
            ss.initialize(args);
        } catch (Exception e) {
            log.error("IsNodeAlive ERROR:" + this.macAddress + ": " + e.getMessage());
            throw new Exception(e.getMessage());
        }
        log.debug("IsNodeAlive OK:" + this.macAddress);

        return null;
    }
}
