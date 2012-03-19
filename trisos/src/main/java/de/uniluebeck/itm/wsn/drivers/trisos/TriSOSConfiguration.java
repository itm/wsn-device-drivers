package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Map;


/**
 * Stores the configuration of the TriSOS device driver.
 * When configuring testbed runtime add configuration parameter in tr.iwsn-testbed.xml.
 * A typical configuration may look like this:
 * <pre>
 * {@code
 * ...
 * <wsn:wsnDevice xmlns:wsn="http://itm.uniluebeck.de/tr/runtime/wsnapp/xml">
 *      <urn>urn:wisebed:cosa-testbed-fhl1:0x140</urn>
 *      <type>trisos</type>
 *      <serialinterface>COM6</serialinterface>
 *      <!-- Full path to the programmer executable. Currently JTAGICEmkII and AVRDragon are tested and used. -->
 *      <configuration key="trisos.programmer.executable" value="C:\\Program Files\\Atmel\\AVR Tools\\JTAGICEmkII\\jtagiceii.exe" />
 *      <!-- Optional baudrate for the serial port. When ommited the baudrate is set to 115200. -->
 *      <configuration key="trisos.serialport.baudrate" value="38400" />
 *      <!-- Place where the binary file for the node is saved. The path is created if it does not exist.-->
 *      <configuration key="trisos.programmer.program.binfile" value="..\\trisos-binfile\\flashMe.elf" />
 *      <!-- The MCU type. For TriSOS currently ATmega2560 or ATxmega128A1 are used. -->
 *      <configuration key="trisos.programmer.device" value="ATxmega128A1" />
 *      <!-- Programming command for the programmer executable. -->
 *      <configuration key="trisos.programmer.program.command" value="trisos.programmer.executable -d trisos.programmer.device -e -pa -ia trisos.programmer.program.binfile" />
 *      <!-- Resetting command for the programmer executable. -->
 *      <configuration key="trisos.programmer.reset.command" value="trisos.programmer.executable -d trisos.programmer.device -R" />
 * </wsn:wsnDevice>
 * ...
 * }
 * </pre>
 * NOTICE: When used together with wsn-device-utils the key/value pairs have to be provided in a property file.
 * @author teublert
 */
public class TriSOSConfiguration {

        /**
         * The configuration data key/value pairs
         */
        private final Map<String, String> configuration;

        /**
         * Executable for the programming device
         */
        private String programExe;

        /**
         * Binary file name
         */
        private String binFileCompletePath;

        /**
         * The device type (MCU type for programmer)
         */
        private String device;
        
	/**
	 * Constructor.
         * Fetches the key/value pairs from the configuration.
         * @param configuration injected by Guice
         */
        @Inject
	public TriSOSConfiguration(@Named("configuration") final Map<String, String> configuration) {
            this.configuration = configuration;
            programExe = configuration.get("trisos.programmer.executable");
            binFileCompletePath = configuration.get("trisos.programmer.program.binfile");
            device = configuration.get("trisos.programmer.device");
        }

        /**
         * The programming command line command as String
         * @return String programming command line command
         */
        public String getProgramCommandString() {
            String programCommand = configuration.get("trisos.programmer.program.command");
            programCommand = programCommand.replace("trisos.programmer.executable", programExe);
            programCommand = programCommand.replace("trisos.programmer.program.binfile", binFileCompletePath);
            programCommand = programCommand.replace("trisos.programmer.device", device);
            return programCommand;
        }

        /**
         * The resetting command line command as String
         * @return String reset command line command
         */
        public String getResetCommandString() {
            String resetCommand = configuration.get("trisos.programmer.reset.command");
            resetCommand = resetCommand.replace("trisos.programmer.executable", programExe);
            resetCommand = resetCommand.replace("trisos.programmer.device", device);
            return resetCommand;
        }

        /**
         * The complete path to the binary file.
         * @return
         */
        public String getBinFileCompletePath() {
            return binFileCompletePath;
        }
}
