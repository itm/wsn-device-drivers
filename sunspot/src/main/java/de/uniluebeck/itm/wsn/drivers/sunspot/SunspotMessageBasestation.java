package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.collect.Multimap;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class SunspotMessageBasestation implements Runnable {
    // Broadcast port on which we listen for sensor samples

    private int HOST_PORT=100;
    private int sendMessagePort=101;
    private Multimap<String, SunspotBaseStationListener> listeners;
    private static final Logger log = LoggerFactory.getLogger(SunspotMessageBasestation.class);
    private BlockingQueue<SunspotMessage> sendMessageQueue;

    public SunspotMessageBasestation(int WSNPort,int SendMessagePort,  Multimap<String, SunspotBaseStationListener> listeners, BlockingQueue<SunspotMessage> sendMessageQueue) {
        this.HOST_PORT = WSNPort;
        this.listeners = listeners;
        this.sendMessagePort=SendMessagePort;
        this.sendMessageQueue=sendMessageQueue;
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

        HashMap<String, Long> logValues = new HashMap<String, Long>();

        int counter=0;
        int missed=0;
        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                rCon.receive(dg);
                String mac = dg.getAddress();  // read sender's Id
                byte[] msg = dg.getData();
                long now = System.currentTimeMillis();
                log.debug("SunspotHostMsg:" + String.valueOf(now) + ":" + mac + ":" + msg.toString() + ":\n");
                long val = payloadAsInteger(msg);
                log.debug("SunspotHostMsg:" + val);

                if (logValues.containsKey(mac) == false) {
                    logValues.put(mac, val);
                    counter++;
                } else {
                    counter++;
                    if (val - (long) logValues.get(mac) > 1) {
                        missed++;
                        log.info("MISSED VALUE from " + mac + " Last Val:"+(long) logValues.get(mac) +" Value:"+val);
                        log.info("MISSED :" +(double) missed/counter);
                    }
                    logValues.put(mac, val);
                }

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


    long payloadAsInteger(byte[] b) {
        long l = 0;
        l |= b[0] & 0xFF;
        l <<= 8;
        l |= b[1] & 0xFF;
        l <<= 8;
        l |= b[2] & 0xFF;
        l <<= 8;
        l |= b[3] & 0xFF;
        return l;

    }
}
