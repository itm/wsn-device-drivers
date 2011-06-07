package org.sunspotworld;

 
import com.sun.spot.io.j2me.radiogram.*;
import java.text.DateFormat;
import javax.microedition.io.*;



public class SunSpotHostApplication {
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
                long now=System.currentTimeMillis();
		String msg=String.valueOf(val);
		System.out.println("SunspotHostMsg:"+String.valueOf(now)+":"+addr+":"+msg+":\n");
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                throw e;
            }
        }
    }


    public static void main(String[] args) throws Exception {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
 
    }
}
