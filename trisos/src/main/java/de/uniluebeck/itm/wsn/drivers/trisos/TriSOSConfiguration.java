package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import java.util.Map;
import javax.annotation.Nullable;


/**
 * Stores the configuration of the TriSOS device driver.
 * 
 * @author teublert
 */
public class TriSOSConfiguration {

        /**
         *
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
         *
         * @param configuration 
         */
        @Inject
	public TriSOSConfiguration(@Named("configuration") final Map<String, String> configuration) {
            this.configuration = configuration;
            programExe = configuration.get("trisos.programmer.executable");
            binFileCompletePath = configuration.get("trisos.programmer.program.binfile");
            device = configuration.get("trisos.programmer.device");
        }

        /**
         * 
         * @return
         */
        public String getProgramCommandString() {
            String programCommand = configuration.get("trisos.programmer.program.command");
            programCommand = programCommand.replace("trisos.programmer.executable", programExe);
            programCommand = programCommand.replace("trisos.programmer.program.binfile", binFileCompletePath);
            programCommand = programCommand.replace("trisos.programmer.device", device);
            return programCommand;
        }

        /**
         *
         * @return
         */
        public String getResetCommandString() {
            String resetCommand = configuration.get("trisos.programmer.reset.command");
            resetCommand = resetCommand.replace("trisos.programmer.executable", programExe);
            resetCommand = resetCommand.replace("trisos.programmer.device", device);
            return resetCommand;
        }

        /**
         * 
         * @return
         */
        public String getBinFileCompletePath() {
            return binFileCompletePath;
        }
}
