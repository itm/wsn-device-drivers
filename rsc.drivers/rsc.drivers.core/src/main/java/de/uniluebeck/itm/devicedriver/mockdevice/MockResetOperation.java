package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;


/**
 * Mock operation for reseting the device.
 * Internal the periodically send of messages is reseted.
 * 
 * @author Malte Legenhausen
 */
public class MockResetOperation extends AbstractOperation<Void> implements ResetOperation {

	/**
	 * A default sleep time before and after the reset.
	 */
	private static final int SLEEP_TIME = 200;
	
	/**
	 * The time that is used for the reset.
	 */
	private static final int RESET_TIME = 1000;
	
	/**
	 * The <code>MockConnection</code> that is used for the reset.
	 */
	private final MockConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The <code>MockConnection</code> that is used for the reset.
	 */
	public MockResetOperation(final MockConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		Thread.sleep(SLEEP_TIME);
		connection.stopAliveRunnable();
		Thread.sleep(RESET_TIME);
		connection.sendMessage("Booting MockDevice...");
		Thread.sleep(SLEEP_TIME);
		connection.scheduleAliveRunnable();
		return null;
	}
}
