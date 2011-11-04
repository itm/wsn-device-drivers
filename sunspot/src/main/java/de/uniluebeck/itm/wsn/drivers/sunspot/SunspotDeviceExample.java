package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;

import java.io.*;
import java.util.HashMap;

public class SunspotDeviceExample {


    public static void main(String[] args) throws IOException, InterruptedException {

        Logging.setDebugLoggingDefaults();

        HashMap<String, String> baseStationConfiguration = new HashMap<String, String>();
        baseStationConfiguration.put("SunspotBuildPath", "/home/evangelos/programs/SunSPOT/sdk-red-090706/build.xml");
        baseStationConfiguration.put("receivingBasestationAppPath", "/home/evangelos/programs/SunSPOT/host/build.xml");
        baseStationConfiguration.put("tempDirectory", "/home/evangelos/programs/SunSPOT/sdk-red-090706/temp");
        baseStationConfiguration.put("BasestationPort", "/dev/ttyACM0");
        baseStationConfiguration.put("sysBinPath", "-sysBin/home/evangelos/programs/SunSPOT/sdk-red-090706/arm");
        baseStationConfiguration.put("libFilePath", "-libFile/home/evangelos/programs/SunSPOT/sdk-red-090706/arm/transducerlib");
        baseStationConfiguration.put("keyStrorePath", "-keyStorePath/home/evangelos/sunspotkeystore");
        baseStationConfiguration.put("workingDirectory", "/home/evangelos/temp");


        PipedInputStream inputStream = new PipedInputStream();
        SunspotModule sb = new SunspotModule(baseStationConfiguration);
        Injector injector = Guice.createInjector(sb, new DeviceModule());

        SunspotBaseStation sbs = injector.getInstance(SunspotBaseStation.class);

        HashMap<String, String> deviceConfiguration = new HashMap<String, String>();
        deviceConfiguration.put("macAddress", "0014.4F01.0000." + "5EF4");
        SunspotDevice sd1 = injector.getInstance(SunspotDevice.class);
        sd1.setConfiguration(deviceConfiguration);
        System.out.println("CONNECT");

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

        System.out.println("0-------------------------------------------------------");

        /*sd1.isConnected(100000, new OperationCallback<Void>() {
            @Override
            public void onExecute() {

            }

            @Override
            public void onSuccess(Void result) {
                System.out.println("IsNoDEALive DONE");

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
        });*/

        System.out.println("1-------------------------------------------------------");



        byte[] sunspotImage = getBytesFromFile(new File("/home/evangelos/programs/SunSPOT/broadcast.jar"));


        for (int i = 0; i < 10; i++) {
            sd1.program(sunspotImage, 100000, new OperationCallback<Void>() {
                @Override
                public void onExecute() {

                }

                @Override
                public void onSuccess(Void result) {
                    System.out.println("Deploy APP DONE");

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


        //  sbs.stop();
        //  System.exit(1);

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

    public static void printInputStream(InputStream inputStream) throws IOException {
        System.out.println("Final -----------------------");
        long length = inputStream.available();
        byte[] bytes = new byte[(int) length];
        inputStream.read(bytes);
        System.out.println("Final >>" + new String(bytes));
        System.out.println("Final -----------------------");
    }
}
