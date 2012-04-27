package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortConnection;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.NoSuchElementException;


/**
 * TelosB connection.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class TelosbSerialPortConnection extends AbstractSerialPortConnection {

	private static final int NORMAL_BAUD_RATE = 115200;

	private static final int PROGRAM_BAUD_RATE = 9600;

	@Inject
	public TelosbSerialPortConnection() {
		setProgramBaudRate(PROGRAM_BAUD_RATE);
		setNormalBaudRate(NORMAL_BAUD_RATE);
		setNormalParityBit(SerialPort.PARITY_NONE);
		setProgramParityBit(SerialPort.PARITY_EVEN);
	}

	@Override
	protected void connectSerialPort(String port) throws NoSuchElementException, PortInUseException, IOException {
		super.connectSerialPort(port);
		try {
			getSerialPort().setSerialPortParams(
					NORMAL_BAUD_RATE,
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
