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
		connection.reset();
		return null;
	}
}
