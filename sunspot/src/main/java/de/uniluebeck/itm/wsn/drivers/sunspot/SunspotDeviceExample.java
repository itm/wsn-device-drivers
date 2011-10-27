package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SunspotDeviceExample {


    public static void main(String[] args) throws IOException {

        Logging.setDebugLoggingDefaults();

        HashMap<String, String> baseStationConfiguration = new HashMap<String, String>();
        baseStationConfiguration.put("SunspotBuildPath", "/home/evangelos/programs/SunSPOT/sdk/build.xml");
        baseStationConfiguration.put("receivingBasestationAppPath", "/home/evangelos/programs/SunSPOT/host/build.xml");
        baseStationConfiguration.put("tempDirectory", "/home/evangelos/programs/SunSPOT/temp");

        baseStationConfiguration.put("commandBasestationPort", "/dev/ttyACM0");
        baseStationConfiguration.put("receivingBasestationPort", "/dev/ttyACM1");


        SunspotModule sb = new SunspotModule(baseStationConfiguration);
        Injector injector = Guice.createInjector(sb, new DeviceModule());
        SunspotBaseStation sbs = injector.getInstance(SunspotBaseStation.class);

        HashMap<String, String> deviceConfiguration = new HashMap<String, String>();
        deviceConfiguration.put("macAddress", "0014.4F01.0000." + "5EF4");

        SunspotDevice sd1 = injector.getInstance(SunspotDevice.class);
        sd1.setConfiguration(deviceConfiguration);
        sd1.connect(null);
        sd1.reset(100000, new OperationCallback<Void>() {
            @Override
            public void onExecute() {

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

      /*  String jar = "/home/evangelos/programs/SunSPOT/broadcast.jar";
        File file = new File(jar);

        byte[] b = getBytesFromFile(file);

        sd1.program(b, 40000, new OperationCallback<Void>() {
            @Override
            public void onExecute() {

            }

            @Override
            public void onSuccess(Void result) {
                System.out.println("Flashed");
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
        */
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }


}
