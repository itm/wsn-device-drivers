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
	 * The configuration that will store the binary image.
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
            byte binData[] = getBinaryImage();

            FileOutputStream os = new FileOutputStream(new File(configuration.getBinFileName()));
            os.write(binData);
            os.close();

            log.trace("Execute: " + configuration.getProgrammerProgramCommandString());

            /* Execute command */
            Process p = Runtime.getRuntime().exec(configuration.getProgrammerProgramCommand());
            BufferedReader process_in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;

            while((line = process_in.readLine()) != null) {
                log.trace(line);
                progressManager.worked(0.1f);
            }

            p.destroy();
        }

        @Override
        public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
            program(progressManager.createSub(0.95f), context);
            return null;
        }

}
