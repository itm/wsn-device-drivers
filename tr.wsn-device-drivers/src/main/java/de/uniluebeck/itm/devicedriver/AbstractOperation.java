package de.uniluebeck.itm.devicedriver;

/**
 * An abstract operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public abstract class AbstractOperation<T> implements Operation<T> {

	private State state;
	
	private int timeout;
	
	private T result;

	protected void setResult(T result) {
		this.result = result;
	}
	
	@Override
	public void cancel() {
		setState(State.CANCELED);
	}

	@Override
	public T getResult() {
		return result;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
