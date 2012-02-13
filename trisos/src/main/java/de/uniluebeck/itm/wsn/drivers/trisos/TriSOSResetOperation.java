package de.uniluebeck.itm.wsn.drivers.trisos;

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
public class TriSOSResetOperation implements ResetOperation {
	

	
	/**
	 * Constructor.
	 * 
	 * @param connection The <code>TriSOSConnection</code> that is used for the reset.
	 */
	@Inject
	public TriSOSResetOperation() {
		
	}
	
	@Override
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		
		return null;
	}
}
