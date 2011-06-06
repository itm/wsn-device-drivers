package de.uniluebeck.itm.wsn.drivers.core.serialport;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.io.LockedInputStream;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice implements Device<SerialPortConnection> {
	
	/**
	 * <code>SerialPortConnection</code> for this device.
	 */
	private final SerialPortConnection connection;
	
	private final LockedInputStream lockedInputStream;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The serial port connection for this device.
	 */
	public AbstractSerialPortDevice(final SerialPortConnection connection) {
		this.connection = connection;
		lockedInputStream = new LockedInputStream(connection.getInputStream());
	}

	@Override
	public SerialPortConnection getConnection() {
		return connection;
	}
	
	/**
	 * Register a created operation for monitoring purposes by the device.
	 * 
	 * @param <T> Return type of the operation.
	 * @param operation The operation object that has to be monitored.
	 */
	protected <T> Operation<T> monitor(final Operation<T> operation) {
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(final StateChangedEvent<T> event) {
				lockedInputStream.setLocked(State.RUNNING.equals(event.getNewState()));
			}
		});
		return operation;
	}
	
	@Override
	public InputStream getInputStream() {
		return lockedInputStream;
	}
}
