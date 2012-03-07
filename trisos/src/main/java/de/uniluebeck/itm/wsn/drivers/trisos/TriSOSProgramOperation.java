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
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The operation for programming the <code>MockDevice</code>.
 * 
 * @author Malte Legenhausen
 */
public class TriSOSProgramOperation extends AbstractProgramOperation {

        /**
         *
         */
         private static final Logger log = LoggerFactory.getLogger(TriSOSProgramOperation.class);
	
	/**
	 * The configuration 
	 */
	private TriSOSConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The configuration of the <code>MockDevice</code>.
	 */
	@Inject
	public TriSOSProgramOperation(TriSOSConfiguration configuration) {
		this.configuration = configuration;
	}

        @ProgrammingMode
	void program(final ProgressManager progressManager, OperationContext context) throws Exception {

            // Fetch bin file ...
            byte binData[] = getBinaryImage();
            File binFile = new File(configuration.getBinFileCompletePath());

            // Write bin file to disk ...
            FileOutputStream os = new FileOutputStream(binFile);
            os.write(binData);
            os.close();

            String programmingCommand = configuration.getProgramCommandString();
            log.info("Execute: " + programmingCommand);
            Process p = Runtime.getRuntime().exec(programmingCommand);

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            String progressString;
            float progress = 0;
            float lastProgress = 0;
            while ((line = bri.readLine()) != null) {
                log.trace(line);
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
            while ((line = bre.readLine()) != null) {
                log.error(line);
            }
            bre.close();
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
