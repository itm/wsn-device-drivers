package de.uniluebeck.itm.wsn.drivers.sunspot;


import com.sun.spot.io.j2me.radiogram.RadiogramConnection;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import java.text.DateFormat;


public class hostruntest {
    // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 100;

    private void run() throws Exception {
        RadiogramConnection rCon;
        Datagram dg;
        DateFormat fmt = DateFormat.getTimeInstance();
        try {
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
            System.err.println("setUp caught " + e.getMessage());
            throw e;
        }

        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                rCon.receive(dg);
                String addr = dg.getAddress();  // read sender's Id
                int val = dg.readInt();         // read the sensor value
                long now = System.currentTimeMillis();
                String msg = String.valueOf(val);
                System.out.println("SunspotHostMsg:" + String.valueOf(now) + ":" + addr + ":" + msg + ":\n");

            } catch (Exception e) {
                System.err.println("Caught " + e + " while reading sensor samples.");
                throw e;
            }
        }
    }


    public static void main(String[] args) throws Exception {
        System.setProperty("SERIAL_PORT", "/dev/ttyACM0");
        Runnable a = new Runnable() {
            @Override
            public void run() {
                try {
                    hostruntest app = new hostruntest();
                    app.run();
                } catch (Exception e) {
                }
            }
        };

        (new Thread(a)).start();
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }


    }
}