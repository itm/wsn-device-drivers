package de.uniluebeck.itm.wsn.drivers.isense;

import gnu.io.PortInUseException;
import gnu.io.SerialPortEventListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.TooManyListenersException;


/**
 * Generic serial port connection type for isense devices.
 * 
 * @author Malte Legenhausen
 */
public class iSenseSerialPortConnection extends SimpleSerialPortConnection implements SerialPortEventListener {
	
	private static final int NORMAL_BAUDRATE = 115200;

	private static final int PROGRAM_BAUDRATE = 38400;
	
	public iSenseSerialPortConnection() {
		setNormalBaudrate(NORMAL_BAUDRATE);
		setProgramBaudrate(PROGRAM_BAUDRATE);
	}
	
	@Override
	protected void connectSerialPort(String port) throws NoSuchElementException, PortInUseException,
			TooManyListenersException, IOException {
		super.connectSerialPort(port);
		setSerialPortMode(SerialPortMode.NORMAL);
	}
}
