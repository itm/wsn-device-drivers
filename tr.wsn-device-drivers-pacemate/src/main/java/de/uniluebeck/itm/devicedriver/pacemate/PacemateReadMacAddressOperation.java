package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.tr.util.StringUtils;

public class PacemateReadMacAddressOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateReadMacAddressOperation.class);
	
	private final PacemateDevice device;
	
	public PacemateReadMacAddressOperation(PacemateDevice device) {
		this.device = device;
	}
	
	@Override
	public MacAddress execute(Monitor monitor) throws Exception {
		// Connection established, read flash header
		final int macStart = 0x2ff8;
		final int macLength = 8;
		
		final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
		readFlashOperation.setAddress(macStart, macLength);
		byte[] header = executeSubOperation(readFlashOperation);
		
		byte[] macUUcode = new byte[4];
		byte[] mac = new byte[8];
		
		byte[] checksum = new byte[header.length - 13 - 2 - 2];
		System.arraycopy(header, 15, checksum, 0, header.length -13 - 2 - 2);
		log.debug("Checksum: " + StringUtils.toHexString(checksum));
		
		System.arraycopy(header, 1, macUUcode, 0, 4);
		byte[] macpart2 = decode(macUUcode);
		mac[0] = macpart2[0];
		mac[1] = macpart2[1];
		mac[2] = macpart2[2];
		
		System.arraycopy(header, 5, macUUcode, 0, 4);
		macpart2 = decode(macUUcode);
		mac[3] = macpart2[0];
		mac[4] = macpart2[1];
		mac[5] = macpart2[2];
		
		System.arraycopy(header, 9, macUUcode, 0, 4);
		macpart2 = decode(macUUcode);
		mac[6] = macpart2[0];
		mac[7] = macpart2[1];

		log.debug("Read raw MAC: " + StringUtils.toHexString(mac));
		final MacAddress macAddress = new MacAddress(mac);
		log.debug("Read MAC: " + macAddress);

		log.debug("Done, result is: " + macAddress);
		return macAddress;
	}

	private static byte[] decode(byte[] temp) {
		final byte [] outbyte = new byte[3];
		outbyte [0] = decodeByte (temp [0]);
	    outbyte [1] = decodeByte (temp [1]);
	    outbyte [0] <<= 2;
	    outbyte [0] |= (outbyte [1] >> 4) & 0x03;
	    outbyte [1] <<= 4;
	    outbyte [2] = decodeByte (temp [2]);
	    outbyte [1] |= (outbyte [2] >> 2) & 0x0F;
	    outbyte [2] <<= 6;
	    outbyte [2] |= decodeByte (temp [3]) & 0x3F;
		
	    //System.out.println(" = "+(int)(outbyte[0] & 0xFF)+" "+(int)(outbyte[1] & 0xFF)+" "+(int)(outbyte[2] & 0xFF));
	    //checksum2 = checksum2 + (int)(outbyte[0] & 0xFF)+(int)(outbyte[1] & 0xFF)+(int)(outbyte[2] & 0xFF);
	    return outbyte;
	}
	
	private static byte decodeByte(final byte b) {
		return b == 0x60 ? 0 : (byte)(b - 0x20); 
	}
}
