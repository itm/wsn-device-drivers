package de.uniluebeck.itm.metadaten.remote.client;


/**
 * Adapter class that provides a null implementation for an <code>AsyncCallback</code>.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of onSuccess.
 */
public class AsyncAdapter<T> implements AsyncCallback<T> {

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
