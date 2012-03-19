package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Map;


/**
 * Stores the configuration of the TriSOS device driver.
 * 
 * @author teublert
 */
public class TriSOSConfiguration {

        /**
         * The configuration data name/value pairs
         */
        private final Map<String, String> configuration;

        /**
         *
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
         * Fetches trisos.programmer.executable (Path to the programmer executable (currently jtagicemkii.exe or AVRDragon.exe)),
         * trisos.programmer.program.binfile (Path to the binary file), and trisos.programmer.device
         * (currently ATmega2560 or ATxmega128A1) from the configuration.
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
