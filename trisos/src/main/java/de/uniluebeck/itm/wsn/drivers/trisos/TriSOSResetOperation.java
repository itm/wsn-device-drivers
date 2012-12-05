package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 * TriSOS operation for resetting the TriSOS node.
 *
 * @author Torsten Teubler
 */
public class TriSOSResetOperation extends TimeLimitedOperation<Void> implements ResetOperation {

	private final TriSOSConfiguration configuration;

        private static final Logger log = LoggerFactory.getLogger(TriSOSProgramOperation.class);

	@Inject
	public TriSOSResetOperation(final TimeLimiter timeLimiter,
                                    @Assisted final long timeoutMillis,
                                    @Assisted @Nullable final OperationListener<Void> voidOperationListener,
                                    final TriSOSConfiguration configuration) {
		super(timeLimiter, timeoutMillis, voidOperationListener);
		this.configuration = configuration;
	}

	@Override
	protected Void callInternal() throws Exception {

		// fetch reset command string
		String resetCommand = configuration.getResetCommandString();
		System.out.println("Execute: " + resetCommand);
		Process p = Runtime.getRuntime().exec(resetCommand);

		BufferedReader readerInputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader readerOutputStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String line;

                progress(0f);

		while ((line = readerInputStream.readLine()) != null) {
			log.trace(configuration.getProgramExe() + ": " + line);
		}
		readerInputStream.close();

		while ((line = readerOutputStream.readLine()) != null) {
			log.trace(configuration.getProgramExe() + ": " + line);
		}
		readerOutputStream.close();

		// wait for process to finish ...
		p.waitFor();
		p.destroy();
		progress(1f);

		return null;
	}
}
