package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * Context of an operation runnable.
 * 
 * @author Malte Legenhausen
 */
public interface OperationContext {

	boolean isCanceled();
	
	/**
	 * Call this method when another <code>OperationRunnable</code> has to be executed while this 
	 * <code>OperationRunnable</code>.
	 * 
	 * @param <R> The return type of the sub <code>OperationRunnable</code>.
	 * @param subRunnable The sub <code>OperationRunnable</code> that has to be executed.
	 * @param progressManager The progress manager for observing the progress.
	 * @return The result of the sub <code>OperationRunnable</code>.
	 * @throws Exception Any exception throws be the runnable.
	 */
	<T> T execute(OperationRunnable<T> subRunnable, ProgressManager progressManager) throws Exception;
	
	<T> T execute(OperationRunnable<T> subRunnable, ProgressManager progressManager, float subFraction) 
			throws Exception;
}
