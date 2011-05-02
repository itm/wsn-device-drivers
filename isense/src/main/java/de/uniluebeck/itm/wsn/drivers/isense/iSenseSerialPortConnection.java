package de.uniluebeck.itm.wsn.drivers.isense;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;


/**
 * Generic serial port connection type for isense devices.
 * 
 * @author Malte Legenhausen
 */
public class iSenseSerialPortConnection extends SimpleSerialPortConnection {
	
	private static final int NORMAL_BAUDRATE = 115200;

	private static final int PROGRAM_BAUDRATE = 38400;
	
	public iSenseSerialPortConnection() {
		setNormalBaudrate(NORMAL_BAUDRATE);
		setProgramBaudrate(PROGRAM_BAUDRATE);
	}
	
	@Override
	protected void connectSerialPort(String port) throws Exception {
		super.connectSerialPort(port);
		setSerialPortMode(SerialPortMode.NORMAL);
	}
}
