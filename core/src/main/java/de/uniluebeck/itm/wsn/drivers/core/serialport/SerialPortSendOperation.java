package de.uniluebeck.itm.wsn.drivers.core.serialport;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;


/**
 * Operation for sending a <code>MessagePacket</code>.
 * 
 * @author Malte Legenhausen
 */
public class SerialPortSendOperation extends AbstractSendOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SerialPortSendOperation.class);
	
	/**
	 * Maximum length of a message.
	 */
	private static final int MAX_LENGTH = 150;
	
	/**
	 * 
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection <code>SerialPortConnection</code> for sending the message.
	 */
	public SerialPortSendOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		LOG.debug("Executing send operation");
		
		final byte content[] = getMessage();
		if (content.length > MAX_LENGTH) {
			LOG.warn("Skipping too large packet (length " + content.length + ")");
			return null;
		}

		final OutputStream outputStream = connection.getOutputStream();
		outputStream.write(content);
		outputStream.flush();
		LOG.debug("Send operation finished");
		return null;
	}

}
