package de.uniluebeck.itm.wsn.drivers.core.operation;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.Connection;


/**
 * {@link Operation} for sending a bytes to a device by copying it to its OutputStream.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class CopyToOutputStreamSendOperation extends AbstractSendOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CopyToOutputStreamSendOperation.class);

	/**
	 * The connection the operation send over.
	 */
	private final Connection connection;

	/**
	 * Constructor.
	 *
	 * @param connection <code>SerialPortConnection</code> for sending the message.
	 */
	@Inject
	public CopyToOutputStreamSendOperation(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {

		LOG.debug("Executing send operation");

		byte content[] = getMessage();
		OutputStream outputStream = connection.getOutputStream();

		if (LOG.isTraceEnabled()) {
			LOG.trace("Writing to device OutputStream: text=\"{}\", binary=\"{}\"",
					StringUtils.replaceNonPrintableAsciiCharacters(new String(content)),
					StringUtils.toHexString(content)
			);
		}

		outputStream.write(content);
		outputStream.flush();

		LOG.debug("Send operation finished");

		return null;
	}

}
