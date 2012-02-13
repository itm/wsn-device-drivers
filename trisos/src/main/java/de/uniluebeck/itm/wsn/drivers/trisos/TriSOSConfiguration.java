package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import java.util.Map;


/**
 * Stores the configuration of the TriSOS device driver.
 * 
 * @author teublert
 */
public class TriSOSConfiguration {

        /**
         *
         */
        Map<String,String> configuration;

        /**
         * Command line command (programming operation)
         */
        private String[] programmerProgramCommand;

        /**
         * Command line command (reset operation)
         */
        private String[] programmerResetCommand;

        /**
         * Binary file name
         */
        private String binFileName;

	/**
	 * Constructor.
         *
         * @param configuration
         */
        @Inject
	public TriSOSConfiguration(Map<String, String> configuration) {
            this.configuration = configuration;
            assembleProgrammerProgramCommand();
            assembleProgrammerResetCommand();
        }

        /**
         *
         */
        private void assembleProgrammerProgramCommand() {
            // Assembling command line command for programming
            int numberOfParams = 0;
            for(; configuration.get("trisos.programmer.program.param." + numberOfParams) != null; ++numberOfParams){;}
            programmerProgramCommand = new String[numberOfParams + 1];
            programmerProgramCommand[0] = configuration.get("trisos.programmer.program.command");
            if( configuration.containsKey(programmerProgramCommand[0]) ) {
                programmerProgramCommand[0] = configuration.get(programmerProgramCommand[0]);
            }
            /* Fetching name of the file to be flashed on the nodes */
            binFileName = configuration.get("trisos.programmer.program.binfile");
            /* Assemble command array */
            for( int i = 0; i < numberOfParams; ++i ) {
                programmerProgramCommand[i + 1] = configuration.get("trisos.programmer.program.param." + i);
                if( configuration.containsKey(programmerProgramCommand[i + 1]) ) {
                    programmerProgramCommand[i + 1] = configuration.get(programmerProgramCommand[i + 1]);
                }
                programmerProgramCommand[i + 1] = programmerProgramCommand[i + 1].replace("trisos.programmer.program.binfile", binFileName);
            }
        }

        /**
         * 
         */
        private void assembleProgrammerResetCommand() {
            // Assembling command line command for resetting
            int numberOfParams = 0;
            for(; configuration.get("trisos.programmer.reset.param." + numberOfParams) != null; ++numberOfParams){;}
            /* Assemble command array */
            programmerResetCommand = new String[numberOfParams + 1];
            programmerResetCommand[0] = configuration.get("trisos.programmer.reset.command");
            if( configuration.containsKey(programmerResetCommand[0]) ) {
                programmerResetCommand[0] = configuration.get(programmerResetCommand[0]);
            }
            /* Assemble command array */
            for( int i = 0; i < numberOfParams; ++i ) {
                programmerResetCommand[i + 1] = configuration.get("trisos.programmer.reset.param." + i);
                if( configuration.containsKey(programmerResetCommand[i + 1]) ) {
                    programmerResetCommand[i + 1] = configuration.get(programmerResetCommand[i + 1]);
                }
            }
        }

        /**
         *
         */
        String getBinFileName() {
            return binFileName;
        }

        /**
         * Getter for programming command line command for processing with exec()
         * @return command for processing with exec()
         */
        String[] getProgrammerProgramCommand() {
            return programmerProgramCommand;
        }

        /**
         * Getter for resetting command line command for processing with exec()
         * @return command for processing with exec()
         */
        String[] getProgrammerResetCommand() {
            return programmerResetCommand;
        }

        /**
         * Getter for programming command line command string
         * @return String for debug purposes
         */
        String getProgrammerProgramCommandString() {
            String ret = new String();
            for( String str : programmerProgramCommand ) {
                ret += str;
            }
            return ret;
        }

        /**
         * Getter for resetting command line command string
         * @return String for debug purposes
         */
        String getProgrammerResetCommandString() {
            String ret = new String();
            for( String str : programmerResetCommand ) {
                ret += str;
            }
            return ret;
        }

        
}
