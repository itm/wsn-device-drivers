package de.uniluebeck.itm.wsn.drivers.trisos;

import gnu.io.PortInUseException;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.NoSuchElementException;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;


/**
 * Generic serial port connection type for isense devices.
 *
 * @author Malte Legenhausen
 */
public class TriSOSSerialPortConnection extends SimpleSerialPortConnection implements SerialPortEventListener {

	private static final int NORMAL_BAUDRATE = 115200;
        private static final int PROGRAM_BAUDRATE = 19200;

	public TriSOSSerialPortConnection() {
            setNormalBaudrate(NORMAL_BAUDRATE);
            setProgramBaudrate(PROGRAM_BAUDRATE);
	}

        public TriSOSSerialPortConnection(final int baudrate) {
            setNormalBaudrate(baudrate);
            setProgramBaudrate(PROGRAM_BAUDRATE);
        }

	@Override
	protected void connectSerialPort(String port) throws NoSuchElementException, PortInUseException, IOException {
		super.connectSerialPort(port);
		setSerialPortMode(SerialPortMode.NORMAL);
	}
}