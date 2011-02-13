package de.uniluebeck.itm.devicedriver.serialport;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;


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
	 *  
	 */
	private static final byte DLE = 0x10;

	/** 
	 *
	 */
	private static final byte[] DLE_STX = new byte[] { DLE, 0x02 };

	/** 
	 * 
	 */
	private static final byte[] DLE_ETX = new byte[] { DLE, 0x03 };
	
	/**
	 * Maximum length of a message.
	 */
	private static final int MAX_LENGTH = 150;
	
	/**
	 * Empty type definition.
	 */
	private static final byte EMPTY_TYPE = (byte) 0xFF;
	
	/**
	 * 
	 */
	private SerialPortConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection <code>SerialPortConnection</code> for sending the message.
	 */
	public SerialPortSendOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		LOG.debug("Executing send operation");
		
		final byte type = (byte) (EMPTY_TYPE & getMessagePacket().getType());
		final byte content[] = getMessagePacket().getContent();

		if (content == null || type > EMPTY_TYPE) {
			LOG.warn("Skipping empty packet or type > 0xFF.");
			return null;
		}
		if (content.length > MAX_LENGTH) {
			LOG.warn("Skipping too large packet (length " + content.length + ")");
			return null;
		}

		final OutputStream outputStream = connection.getOutputStream();
		
		LOG.debug("Sending start signal DLE STX");
		outputStream.write(DLE_STX);

		LOG.debug("Sending the type escaped");
		outputStream.write(type);
		if (type == DLE) {
			outputStream.write(DLE);
		}

		LOG.debug("Transmiting each byte escaped");
		for (int i = 0; i < content.length; ++i) {
			outputStream.write(content[i]);
			if (content[i] == DLE) {
				outputStream.write(DLE);
			}
		}

		LOG.debug("Sending final DLT ETX");
		outputStream.write(DLE_ETX);
		outputStream.flush();
		LOG.debug("Send operation finished");
		return null;
	}

}
