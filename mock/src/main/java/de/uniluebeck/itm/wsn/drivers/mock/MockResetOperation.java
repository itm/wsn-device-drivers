package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;


/**
 * Mock operation for reseting the connection.
 * Internal the periodically send of messages is reseted.
 * 
 * @author Malte Legenhausen
 */
public class MockResetOperation implements ResetOperation {

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
	@Inject
	public MockResetOperation(MockConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		Thread.sleep(SLEEP_TIME);
		connection.stopAliveRunnable();
		Thread.sleep(RESET_TIME);
		connection.sendMessage("Booting MockDevice...");
		Thread.sleep(SLEEP_TIME);
		connection.scheduleAliveRunnable();
		return null;
	}
}
