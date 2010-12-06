package de.uniluebeck.itm.devicedriver.async;


/**
 * Adapter class that provides a null implementation for an <code>AsyncCallback</code>.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of onSuccess.
 */
public class AsyncAdapter<T> implements AsyncCallback<T> {

	@Override
	public void onExecute() {
		
	}
	
	@Override
	public void onProgressChange(float fraction) {
		
	}

	@Override
	public void onSuccess(T result) {
		
	}

	@Override
	public void onCancel() {
		
	}

	@Override
	public void onFailure(Throwable throwable) {
		
	}
}
