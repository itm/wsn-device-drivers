package de.uniluebeck.itm.devicedriver;

/**
 * An abstract operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public abstract class AbstractOperation<T> implements Operation<T> {
	
	/**
	 * Boolean thats stores if the operatio has to be canceled.
	 */
	private boolean canceled;
	
	@Override
	public void cancel() {
		canceled = true;
	}
	
	@Override
	public boolean isCanceled() {
		return canceled;
	}
}
