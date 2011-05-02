package de.uniluebeck.itm.wsn.drivers.telosb;

import gnu.io.SerialPort;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;


/**
 * TelosB connection.
 * 
 * @author Malte Legenhausen
 */
public class TelosbSerialPortConnection extends SimpleSerialPortConnection {

	private static final int NORMAL_BAUDRATE = 115200;
	private static final int PROGRAM_BAUDRATE = 9600;

	public TelosbSerialPortConnection() {
		setProgramBaudrate(PROGRAM_BAUDRATE);
		setNormalBaudrate(NORMAL_BAUDRATE);
		setNormalParityBit(SerialPort.PARITY_NONE);
		setProgramParitiyBit(SerialPort.PARITY_EVEN);
	}	
	
	@Override
	protected void connectSerialPort(String port) throws Exception {
		super.connectSerialPort(port);
		getSerialPort().setSerialPortParams(NORMAL_BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
		getSerialPort().setRTS(true);
		getSerialPort().setDTR(true);
	}
}
