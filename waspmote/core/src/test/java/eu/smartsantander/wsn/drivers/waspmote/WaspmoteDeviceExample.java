package eu.smartsantander.wsn.drivers.waspmote;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.NodeAddressingHelper;

import java.util.HashMap;

public class WaspmoteDeviceExample {

    public static void main(String[] args) {
        HashMap<String, String> h1 = new HashMap<String, String>();
        h1.put("nodeID", "0x000f");
        h1.put("mac_Digimesh", "00:15:8D:00:33:54:23:67");
        h1.put("mac_802.15.4", "00:15:8D:00:00:04:7D:50");
        h1.put("uri", "/dev/ttyTestbedRuntime");
        HashMap<String, String> h2 = new HashMap<String, String>();
        h2.put("nodeID", "0x000c");
        h2.put("mac_Digimesh", "00:15:8D:00:38:74:03:78");
        h2.put("mac_802.15.4", "00:15:8D:00:00:03:68:2E");
        h2.put("uri", "/dev/ttyTestbedRuntime");


        Injector i1 = Guice.createInjector(new DeviceModule(), new WaspmoteModule(h1));
        Injector i2 = Guice.createInjector(new DeviceModule(), new WaspmoteModule(h2));

        NodeAddressingHelper help1 = i1.getInstance(NodeAddressingHelper.class);
        System.out.println("help1=" + help1);
        NodeAddressingHelper help2 = i2.getInstance(NodeAddressingHelper.class);
        System.out.println("help2=" + help2);
    }
}
