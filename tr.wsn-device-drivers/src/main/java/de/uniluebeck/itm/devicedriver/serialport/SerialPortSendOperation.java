package de.uniluebeck.itm.devicedriver.serialport;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;

public class SerialPortSendOperation extends AbstractSendOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(SerialPortSendOperation.class);
	
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
	
	private SerialPortConnection connection;
	
	public SerialPortSendOperation(SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		log.debug("Executing send operation");
		
		final byte type = (byte) (0xFF & messagePacket.getType());
		final byte content[] = messagePacket.getContent();

		if (content == null || type > 0xFF) {
			log.warn("Skipping empty packet or type > 0xFF.");
			return null;
		}
		if (content.length > 150) {
			log.warn("Skipping too large packet (length " + content.length + ")");
			return null;
		}

		final OutputStream outputStream = connection.getOutputStream();
		
		log.debug("Sending start signal DLE STX");
		outputStream.write(DLE_STX);

		log.debug("Sending the type escaped");
		outputStream.write(type);
		if (type == DLE) {
			outputStream.write(DLE);
		}

		log.debug("Transmiting each byte escaped");
		for (int i = 0; i < content.length; ++i) {
			outputStream.write(content[i]);
			if (content[i] == DLE) {
				outputStream.write(DLE);
			}
		}

		log.debug("Sending final DLT ETX");
		outputStream.write(DLE_ETX);
		outputStream.flush();
		log.debug("Send operation finished");
		return null;
	}

}
