package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortConnection;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TelosB connection.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class TelosbSerialPortConnection extends AbstractSerialPortConnection {

	private static final Logger log = LoggerFactory.getLogger(TelosbSerialPortConnection.class);

	public static final int NORMAL_BAUD_RATE = 115200;

	public static final int PROGRAM_BAUD_RATE = 9600;

	public static final int NORMAL_PARITY_BIT = SerialPort.PARITY_NONE;

	public static final int PROGRAM_PARITY_BIT = SerialPort.PARITY_EVEN;

	@Inject
	public TelosbSerialPortConnection() {
		setProgramBaudRate(PROGRAM_BAUD_RATE);
		setNormalBaudRate(NORMAL_BAUD_RATE);
		setNormalParityBit(NORMAL_PARITY_BIT);
		setProgramParityBit(PROGRAM_PARITY_BIT);
	}

	@Override
	public void setSerialPortMode(final SerialPortMode mode) {

		log.trace("TelosbSerialPortConnection.setSerialPortMode(mode={})", mode);

		try {

			final int baudrate = mode == SerialPortMode.PROGRAM ? programBaudRate : normalBaudRate;
			final int parityBit = mode == SerialPortMode.PROGRAM ? programParityBit : normalParityBit;

			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parityBit);

			log.trace("baudrate={},dataBits={},stopBits={},parityBit={}",
					serialPort.getBaudRate(),
					serialPort.getDataBits(),
					serialPort.getStopBits(),
					serialPort.getParity()
			);

		} catch (final UnsupportedCommOperationException e) {
			log.warn("Problem while setting serial port params.", e);
		}

		serialPort.setDTR(true);
		serialPort.setRTS(true);

		log.debug("COM-Port parameters set to baud rate: " + serialPort.getBaudRate());
	}
}
