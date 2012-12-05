package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;


/**
 * The operation for programming the <code>TriSOSDevice</code>.
 *
 * @author Torsten Teubler
 */
public class TriSOSProgramOperation extends AbstractProgramOperation {

	private static final Logger log = LoggerFactory.getLogger(TriSOSProgramOperation.class);

	private final TriSOSConfiguration configuration;

	@Inject
	public TriSOSProgramOperation(final TimeLimiter timeLimiter, 
                                        @Assisted final byte[] binaryImage,
                                        @Assisted final long timeoutMillis,
					@Assisted @Nullable final OperationListener<Void> operationCallback,
					final TriSOSConfiguration configuration) {
		super(timeLimiter, binaryImage, timeoutMillis, operationCallback);
		this.configuration = configuration;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		// Complete path with binary file
		String completePath = configuration.getBinFileCompletePath();
		// Path to binary file (without binary file)
		String filePath = completePath.substring(0, completePath.lastIndexOf(File.separator));
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

                progress(0f);
		// Execute programmer device executable ...
		Process p = Runtime.getRuntime().exec(programmingCommand);

		BufferedReader readerStdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader readerStdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;

		// Handle output from programmer device executable
		while ((line = readerStdIn.readLine()) != null) {
			log.trace(configuration.getProgramExe() + ": " + line);
		}
		readerStdIn.close();

		// Error output from programmer device executable
		while ((line = readerStdErr.readLine()) != null) {
			log.error(configuration.getProgramExe() + ": " + line);
		}
		readerStdErr.close();

		// Wait for process to finish ...
		p.waitFor();
		log.trace("Done: " + programmingCommand);
		p.destroy();

		progress(1f);
		return null;
	}
}
