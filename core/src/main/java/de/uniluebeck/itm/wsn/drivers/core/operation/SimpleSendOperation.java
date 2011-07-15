package de.uniluebeck.itm.wsn.drivers.core.operation;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.Connection;


/**
 * Operation for sending a <code>MessagePacket</code>.
 *
 * @author Malte Legenhausen
 */
public class SimpleSendOperation extends AbstractSendOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SimpleSendOperation.class);

	/**
	 * Maximum length of a message.
	 */
	private static final int DEFAULT_MAX_LENGTH = 150;

	/**
	 * The connection the operation send over.
	 */
	private final Connection connection;
	
	/**
	 * The maximum length of the message.
	 */
	private int maxLength = DEFAULT_MAX_LENGTH;

	/**
	 * Constructor.
	 *
	 * @param connection <code>SerialPortConnection</code> for sending the message.
	 */
	@Inject
	public SimpleSendOperation(Connection connection) {
		this.connection = connection;
	}
	
	@Inject(optional = true)
	public void setMaxLength(@MaxMessageLength int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		LOG.debug("Executing send operation");

		byte content[] = getMessage();
		if (content.length > maxLength) {
			LOG.warn("Skipping too large packet (length " + content.length + ")");
			return null;
		}

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
