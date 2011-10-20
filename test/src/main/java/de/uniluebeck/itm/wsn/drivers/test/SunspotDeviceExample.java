package de.uniluebeck.itm.wsn.drivers.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;
import de.uniluebeck.itm.wsn.drivers.sunspot.SunspotBaseStation;
import de.uniluebeck.itm.wsn.drivers.sunspot.SunspotDevice;
import de.uniluebeck.itm.wsn.drivers.sunspot.SunspotModule;

import java.io.IOException;
import java.util.HashMap;

public class SunspotDeviceExample {


	public static void main(String[] args) throws IOException {

        Logging.setDebugLoggingDefaults();

        HashMap<String,String> baseStationConfiguration=new HashMap<String, String>();
        baseStationConfiguration.put("SunspotBuildPath", "/home/evangelos/programs/SunSPOT/sdk/build.xml");
        baseStationConfiguration.put("receivingBasestationAppPath", "/home/evangelos/workspace/testbed-command-line/scripts/host/build.xml");



		SunspotModule sb = new SunspotModule(baseStationConfiguration);
        Injector injector = Guice.createInjector(sb, new DeviceModule());
        SunspotBaseStation sbs = injector.getInstance(SunspotBaseStation.class);

        HashMap<String,String> deviceConfiguration = new HashMap<String, String>();
        deviceConfiguration.put("macAddress", "0014.4F01.0000." + "5EF4");

        SunspotDevice sd1 = injector.getInstance(SunspotDevice.class);
        sd1.setConfiguration(deviceConfiguration);
        sd1.connect(null);
        sd1.reset(100000, new OperationCallback<Void>() {
            @Override
            public void onExecute() {
                throw (new UnsupportedOperationException());
            }

            @Override
            public void onSuccess(Void result) {
                System.out.println("RESETed");
            }

            @Override
            public void onCancel() {
                throw (new UnsupportedOperationException());
            }

            @Override
            public void onFailure(Throwable throwable) {
                throw (new UnsupportedOperationException());
            }

            @Override
            public void onProgressChange(float fraction) {
                throw (new UnsupportedOperationException());
            }
        });
	}

}
