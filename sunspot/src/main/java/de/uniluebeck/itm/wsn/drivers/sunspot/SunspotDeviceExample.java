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
        baseStationConfiguration.put("ControlRadiogramPort", "101");
        baseStationConfiguration.put("ReceivingRadiogramPort", "100");


        PipedInputStream inputStream = new PipedInputStream();
        SunspotModule sb = new SunspotModule(baseStationConfiguration);
        Injector injector = Guice.createInjector(sb, new DeviceModule());

        SunspotBaseStation sbs = injector.getInstance(SunspotBaseStation.class);

        HashMap<String, String> deviceConfiguration = new HashMap<String, String>();
        deviceConfiguration.put("macAddress", "0014.4F01.0000." + "4C98");
        SunspotDevice sd1 = injector.getInstance(SunspotDevice.class);
        sd1.setConfiguration(deviceConfiguration);
        System.out.println("CONNECT");

        sd1.connect(null);

        System.out.println("0-------------------------------------------------------");

        sd1.isConnected(100000, new OperationCallback<Void>() {
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
        });




        sd1.send(intToByte(1821), 100000, new OperationCallback<Void>() {
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
        });


           byte[] sunspotImage = getBytesFromFile(new File("/home/evangelos/programs/SunSPOT/broadcast2.jar"));

        for (int i = 0; i < 10; i++) {
            System.out.println("1-------------------------------------------------------");
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

            System.out.println("2-------------------------------------------------------");
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


        }

        sd1.send(intToByte(0), 100000, new OperationCallback<Void>() {
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
        });


        Thread.sleep(1000 * 60 * 3);
        sbs.stop();
        System.exit(1);

    }

    public static byte[] intToByte(int input) {
        byte[] conv = new byte[4];
        conv[3] = (byte) (input & 0xff);
        input >>= 8;
        conv[2] = (byte) (input & 0xff);
        input >>= 8;
        conv[1] = (byte) (input & 0xff);
        input >>= 8;
        conv[0] = (byte) input;
        return conv;
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
