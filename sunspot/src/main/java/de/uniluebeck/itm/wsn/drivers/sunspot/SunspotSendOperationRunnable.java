package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.sun.spot.client.ui.SunspotCommandUI;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

public class SunspotSendOperationRunnable implements ProgramOperation {
    private byte[] message;
    private static final Logger log = LoggerFactory.getLogger(SunspotSendOperationRunnable.class);
    private String macAddress;
    private int CommandRadiogramPort;


    public SunspotSendOperationRunnable(String macAddress, byte[] message, int CommandRadiogramPort) {
        this.macAddress = macAddress;
        this.message = message;
        this.CommandRadiogramPort = CommandRadiogramPort;

    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
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


    @Override
    public void setBinaryImage(byte[] data) {
        this.message = data;
    }

    private void start() throws Exception {

        RadiogramConnection dgConnection = null;
        Datagram dg = null;
        dgConnection = (RadiogramConnection) Connector.open("radiogram://" + this.macAddress + ":" + this.CommandRadiogramPort);
        dgConnection.setMaxBroadcastHops(1);
        dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
        dg.reset();
        dg.write(message);
        dgConnection.send(dg);
        System.out.println("Broadcast is going through");
    }
}


