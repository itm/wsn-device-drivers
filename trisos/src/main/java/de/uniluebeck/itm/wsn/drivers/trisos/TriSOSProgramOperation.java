package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;

import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The operation for programming the <code>TriSOSDevice</code>.
 * 
 * @author Torsten Teubler
 */
public class TriSOSProgramOperation extends AbstractProgramOperation {

        /**
         * The logger
         */
         private static final Logger log = LoggerFactory.getLogger(TriSOSProgramOperation.class);
	
	/**
	 * The configuration 
	 */
	private TriSOSConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration injected by Guice.
         * The configuration of the <code>TriSOSDevice</code>.
	 */
	@Inject
	public TriSOSProgramOperation(TriSOSConfiguration configuration) {
		this.configuration = configuration;
	}

        @ProgrammingMode
	void program(final ProgressManager progressManager, OperationContext context) throws Exception {

            // Complete path with binary file
            String completePath = configuration.getBinFileCompletePath();
            // Path to binary file (without binary file)
            String filePath = completePath.substring(0,completePath.lastIndexOf(File.separator));
            // Create directories
            (new File(filePath)).mkdirs();
            // File object for binary file
            File binFile = new File(completePath);

            // Fetch binary file
            byte binData[] = getBinaryImage();
            // Write bin file to disk ...
            FileOutputStream os = new FileOutputStream(binFile);
            os.write(binData);
            os.close();

            // Fetching programming command string ...
            String programmingCommand = configuration.getProgramCommandString();
            log.info("Execute: " + programmingCommand);
            // Execute programmer device executable ...
            Process p = Runtime.getRuntime().exec(programmingCommand);

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            String progressString;
            // Overall progress
            float progress = 0;
            // Progress of the last step
            float lastProgress = 0;
            // Hadle output from programmer device executable
            while ((line = bri.readLine()) != null) {
                log.trace(line);
                // Parsing progress output from the programmer device executable
                // and put it into the progress manager
                if( line.contains("Programming FLASH: ") ) {
                    progressString = line.replace("Programming FLASH: ", "");
                    progressString = progressString.replace("%", "");
                    progress = Float.parseFloat(progressString);
                    progress = (float)(progress/100f);
                    progressManager.worked(progress - lastProgress);
                    lastProgress = progress;
                }
            }
            bri.close();
            // Error output from programmer device executable
            while ((line = bre.readLine()) != null) {
                log.error(line);
            }
            bre.close();
            // Wait for process to finish ...
            p.waitFor();
            log.trace("Done: " + programmingCommand);
            p.destroy();
            progressManager.done();
        }

        @Override
        public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
            program(progressManager.createSub(1.0f), context);
            return null;
        }

}
