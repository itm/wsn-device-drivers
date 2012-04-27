package de.uniluebeck.itm.wsn.drivers.isense;

import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortConnection;
import gnu.io.PortInUseException;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.NoSuchElementException;


/**
 * Generic serial port connection type for isense devices.
 * 
 * @author Malte Legenhausen
 */
public class iSenseSerialPortConnection extends AbstractSerialPortConnection implements SerialPortEventListener {
	
	private static final int NORMAL_BAUD_RATE = 115200;

	private static final int PROGRAM_BAUD_RATE = 38400;
	
	public iSenseSerialPortConnection() {
		setNormalBaudRate(NORMAL_BAUD_RATE);
		setProgramBaudRate(PROGRAM_BAUD_RATE);
	}
	
	@Override
	protected void connectSerialPort(String port) throws NoSuchElementException, PortInUseException, IOException {
		super.connectSerialPort(port);
		setSerialPortMode(SerialPortMode.NORMAL);
	}
}
