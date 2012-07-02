package de.uniluebeck.itm.wsn.drivers.telosb;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.TooManyListenersException;


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
	protected void connectSerialPort(String port) throws NoSuchElementException, PortInUseException,
			TooManyListenersException, IOException {

		super.connectSerialPort(port);

		try {

			getSerialPort().setSerialPortParams(
					NORMAL_BAUDRATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE
			);

		} catch (UnsupportedCommOperationException e) {
			throw new IOException(e);
		}
		getSerialPort().setRTS(true);
		getSerialPort().setDTR(true);
	}
}
