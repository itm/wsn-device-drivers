package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;


/**
 * A manager for managing of an <code>LockedInputStream</code>.
 * 
 * @author Malte Legenhausen
 */
public class LockedInputStreamManager implements ConnectionListener, HasInputStream {

	private LockedInputStream inputStream;
	
	@Override
	public void onConnectionChange(final ConnectionEvent event) {
		if (event.isConnected()) {
			final Connection connection = (Connection) event.getSource();
			inputStream = new LockedInputStream(connection.getInputStream());
		} else {
			inputStream = null;
		}
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * Add an operation for automatic locking when the operation changed to running state.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be monitored.
	 * @return The operation that will be monitored.
	 */
	public <T> Operation<T> monitor(final Operation<T> operation) {
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(final StateChangedEvent<T> event) {
				if (inputStream != null) {
					inputStream.setLocked(State.RUNNING.equals(event.getNewState()));
				}
			}
		});
		return operation;
	}
}
