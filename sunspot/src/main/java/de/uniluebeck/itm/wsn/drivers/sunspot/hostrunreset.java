package de.uniluebeck.itm.wsn.drivers.sunspot;


import com.sun.spot.client.ui.SunspotCommandUI;
import com.sun.spot.peripheral.radio.RadioFactory;
import de.uniluebeck.itm.wsn.drivers.core.util.JarUtil;

public class hostrunreset {
    // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 100;


    public static void main(String[] args) throws Exception {
/*        JarUtil.loadLibrary("rxtxSerial");
        System.setProperty("SERIAL_PORT", "/dev/ttyACM0");
        RadioFactory.getRadioPolicyManager();
        System.out.println(RadioFactory.getRadioPolicyManager().getChannelNumber());

        final SunspotCommandUI ss = new SunspotCommandUI();  //  RadioFactory.getRadioPolicyManager();

        *//*   [java] 'com.sun.spot.client.ui.SpotClientCommandLineUI'
             [java] -app/home/evangelos/programs/SunSPOT/sdk/suite/image'
             [java] '-sysBin/home/evangelos/programs/SunSPOT/sdk/arm'
             [java] '-libFile/home/evangelos/programs/SunSPOT/sdk/arm/transducerlib'
             [java] '-keyStorePath/home/evangelos/sunspotkeystore'
             [java] '-p/dev/ttyACM1'
             [java] '-i/dev/ttyACM0, /dev/ttyACM1'
             [java] '-F/home/evangelos/programs/SunSPOT/sdk/temp/spot-temp-1193595215'
             [java] '-remote.address=0014.4F01.0000.5EF4'

        *//*

 *//*       String[] sstrings = new String[7];
                      sstrings[0] = "-sysBin/home/akribopo/SunSPOT/sdk-red-090706/arm";
                      sstrings[1] = "-libFile/home/akribopo/SunSPOT/sdk-red-090706/arm/transducerlib";
                      sstrings[2] = "-keyStorePath/home/akribopo/sunspotkeystore";
                      sstrings[3] = "-p/dev/ttyACM0";
                      sstrings[4] = "-i/dev/ttyACM0";
                      sstrings[5] = "-f/home/akribopo/tmp/skata";
                      sstrings[6] = "-remote.address=0014.4F01.0000.616C";*//*



        ss.initialize1(new String[]{
                "-sysBin/home/evangelos/programs/SunSPOT/sdk/arm",
                "-libFile/home/evangelos/programs/SunSPOT/sdk/arm/transducerlib",
                "-keyStorePath/home/evangelos/sunspotkeystore",
                "-p/dev/ttyACM0",
                "-remote.address=0014.4F01.0000.5EF4",
                "-i/dev/ttyACM0",
                "-f/home/evangelos/programs/SunSPOT/temp/reboot"

        });*/
        JarUtil.loadLibrary("rxtxSerial");
        System.setProperty("SERIAL_PORT", "/dev/ttyACM0");


        RadioFactory.getRadioPolicyManager();
        System.out.println(RadioFactory.getRadioPolicyManager().getChannelNumber());

        final SunspotCommandUI ss = new SunspotCommandUI();
        Runnable rs = new Runnable() {
            public void run() {

               try{
                String[] sstrings = new String[7];
                sstrings[0] = "-sysBin/home/evangelos/programs/SunSPOT/sdk/arm";
                sstrings[1] = "-libFile/home/evangelos/programs/SunSPOT/sdk/transducerlib";
                sstrings[2] = "-keyStorePath/home/evangelos/sunspotkeystore";
                sstrings[3] = "-p/dev/ttyACM0";
                sstrings[4] = "-i/dev/ttyACM0";
                sstrings[5] = "-f/home/evangelos/programs/SunSPOT/temp/reboot";
                sstrings[6] = "-remote.address=0014.4F01.0000.5EF4";

                ss.initialize(sstrings);
               }catch(Exception e){
                   System.out.println(">>"+e.getMessage());
               }

            }
        };
        (new Thread(rs)).start();
        System.out.println("Before sleep");
        Thread.sleep(20000);
        System.out.println("2nd time");
        (new Thread(rs)).start();


    }
}