package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortConnection;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.io.IOException;
import java.util.Map;


public class TriSOSSerialPortConnection extends AbstractSerialPortConnection {

	private static final int NORMAL_BAUD_RATE = 115200;

	private static final int PROGRAM_BAUD_RATE = 19200;

	@Inject
	public TriSOSSerialPortConnection(final Map<String, String> configuration) {

		int baudrate = NORMAL_BAUD_RATE;
		String baudrateString = configuration.get(TriSOSConfiguration.TRISOS_SERIALPORT_BAUDRATE);
		if (baudrateString != null) {
			try {
				baudrate = Integer.parseInt(baudrateString);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			}
		}

		setNormalBaudRate(baudrate);
		setProgramBaudRate(PROGRAM_BAUD_RATE);
	}
}