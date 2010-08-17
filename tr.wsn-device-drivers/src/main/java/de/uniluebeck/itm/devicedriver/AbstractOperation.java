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
	 * The result of the operation.
	 */
	private T result;
	
	/**
	 * Boolean thats stores if the operatio has to be canceled.
	 */
	private boolean canceled;
	
	/**
	 * Sets the result of the operation.
	 * 
	 * @param result The result of type T.
	 */
	protected void setResult(T result) {
		this.result = result;
	}
	
	@Override
	public T getResult() {
		return result;
	}
	
	@Override
	public void cancel() {
		canceled = true;
	}
	
	@Override
	public boolean isCanceled() {
		return canceled;
	}
}
