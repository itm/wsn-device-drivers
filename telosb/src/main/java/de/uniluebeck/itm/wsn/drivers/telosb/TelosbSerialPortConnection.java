package de.uniluebeck.itm.wsn.drivers.telosb;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.TooManyListenersException;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;


/**
 * TelosB connection.
 *
 * @author Malte Legenhausen
 */
public class TelosbSerialPortConnection extends SimpleSerialPortConnection {

	private static final int NORMAL_BAUDRATE = 115200;

	private static final int PROGRAM_BAUDRATE = 9600;

	@Inject
	public TelosbSerialPortConnection() {
		setProgramBaudrate(PROGRAM_BAUDRATE);
		setNormalBaudrate(NORMAL_BAUDRATE);
		setNormalParityBit(SerialPort.PARITY_NONE);
		setProgramParitiyBit(SerialPort.PARITY_EVEN);
	}
	
	@Override
	public int[] getChannels() {
		return null;
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
					SerialPort.PARITY_EVEN
			);

		} catch (UnsupportedCommOperationException e) {
			throw new IOException(e);
		}
		getSerialPort().setRTS(true);
		getSerialPort().setDTR(true);
	}
}
