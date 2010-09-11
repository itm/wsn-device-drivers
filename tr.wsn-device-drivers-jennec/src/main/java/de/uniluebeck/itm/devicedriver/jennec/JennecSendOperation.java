package de.uniluebeck.itm.devicedriver.jennec;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;

public class JennecSendOperation extends AbstractSendOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecSendOperation.class);
	
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
	
	public JennecSendOperation(SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(Monitor monitor) {
		log.info("Executing send operation");
		
		byte type = (byte) (0xFF & messagePacket.getType());
		byte b[] = messagePacket.getContent();

		if (b == null || type > 0xFF) {
			log.warn("Skipping empty packet or type > 0xFF.");
			return null;
		}
		if (b.length > 150) {
			log.warn("Skipping too large packet (length " + b.length + ")");
			return null;
		}

		final OutputStream outputStream = connection.getOutputStream();
		
		try {
			log.debug("Sending start signal DLE STX");
			outputStream.write(DLE_STX);
	
			log.debug("Sending the type escaped");
			outputStream.write(type);
			if (type == DLE) {
				outputStream.write(DLE);
			}
	
			log.debug("Transmiting each byte escaped");
			for (int i = 0; i < b.length; ++i) {
				outputStream.write(b[i]);
				if (b[i] == DLE) {
					outputStream.write(DLE);
				}
			}
	
			log.debug("Sending final DLT ETX");
			outputStream.write(DLE_ETX);
			outputStream.flush();
			log.info("Send operation finished");
		} catch(IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}

}
