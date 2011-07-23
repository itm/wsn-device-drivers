package de.uniluebeck.itm.wsn.drivers.core.operation;




/**
 * A device operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public interface OperationRunnable<T> {
	
	/**
	 * Method that is called when the operation has to be executed.
	 * 
	 * @param progressManager The progress manager for this operation.
	 * @return The result of the <code>OperationRunnable</code> execution.
	 * @throws Exception Any exception that can occur during an operation execution.
	 */
	T run(ProgressManager progressManager, OperationContext context) throws Exception;
}
