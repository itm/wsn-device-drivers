package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.collect.Multimap;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import java.text.DateFormat;
import java.util.Collection;

public class SunspotMessageBasestation implements Runnable {
    // Broadcast port on which we listen for sensor samples

    private int HOST_PORT = 100;
    private Multimap<String, SunspotBaseStationListener> listeners;
    private static final Logger log = LoggerFactory.getLogger(SunspotMessageBasestation.class);

    public SunspotMessageBasestation(int WSNPort, String devicePort, Multimap<String, SunspotBaseStationListener> listeners) {
        this.HOST_PORT = WSNPort;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        RadiogramConnection rCon;
        Datagram dg;
        DateFormat fmt = DateFormat.getTimeInstance();
        try {
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
            log.error("setUp caught " + e.getMessage());
            throw e;
        }

        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                rCon.receive(dg);
                String mac = dg.getAddress();  // read sender's Id
                byte[] msg = dg.getData();
                long now = System.currentTimeMillis();
                log.debug("SunspotHostMsg:" + String.valueOf(now) + ":" + mac + ":" + msg.toString() + ":\n");
                Collection<SunspotBaseStationListener> sd = listeners.get(mac);
                for (SunspotBaseStationListener list : sd) {
                    list.messageReceived(msg);
                }
            } catch (Exception e) {
                log.error("Caught " + e + " while reading sensor samples.");
                throw e;
            }
        }
    }
}
