package de.uniluebeck.itm.wsn.drivers.core.operation;



/**
 * The root progress manager.
 * 
 * @author Malte Legenhausen
 */
public class RootProgressManager extends AbstractProgressManager {
	
	/**
	 * The callback instance that is used for response.
	 */
	private final ProgressCallback callback;
	
	/**
	 * Constructor.
	 * 
	 * @param callback ProgressCallback instance.
	 */
	public RootProgressManager(final ProgressCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public ChildProgressManager createSub(final float amount) {
		return new ChildProgressManager(this, amount);
	}
	
	@Override
	protected void onWorked(final float progress, final float worked) {
		callback.onProgressChange(progress);
	}
}
