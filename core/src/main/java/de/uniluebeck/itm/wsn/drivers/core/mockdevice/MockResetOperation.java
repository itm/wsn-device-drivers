package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;


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
	public Void execute(final ProgressManager progressManager) throws Exception {
		Thread.sleep(SLEEP_TIME);
		connection.stopAliveRunnable();
		Thread.sleep(RESET_TIME);
		connection.sendMessage("Booting MockDevice...");
		Thread.sleep(SLEEP_TIME);
		connection.scheduleAliveRunnable();
		return null;
	}
}
