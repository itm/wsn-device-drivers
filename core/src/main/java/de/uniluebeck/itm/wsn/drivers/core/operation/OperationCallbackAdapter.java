package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * Adapter class that provides a null implementation for an <code>OperationCallback</code>.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of onSuccess.
 */
public class OperationCallbackAdapter<T> implements OperationCallback<T> {

	@Override
	public void onExecute() {
		
	}
	
	@Override
	public void onProgressChange(final float fraction) {
		
	}

	@Override
	public void onSuccess(final T result) {
		
	}

	@Override
	public void onCancel() {
		
	}

	@Override
	public void onFailure(final Throwable throwable) {
		
	}
}
