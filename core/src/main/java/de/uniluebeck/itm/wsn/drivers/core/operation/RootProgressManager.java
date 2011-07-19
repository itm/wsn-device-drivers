package de.uniluebeck.itm.wsn.drivers.core.operation;



/**
 * The root progress manager.
 * 
 * @author Malte Legenhausen
 */
public class RootProgressManager extends AbstractProgressManager {
	
	/**
	 * The monitor instance that is used for response.
	 */
	private final ProgressCallback monitor;
	
	/**
	 * Constructor.
	 * 
	 * @param monitor ProgressCallback instance.
	 */
	public RootProgressManager(final ProgressCallback monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public ChildProgressManager createSub(final float amount) {
		return new ChildProgressManager(this, amount);
	}
	
	@Override
	protected void onWorked(final float progress, final float worked) {
		monitor.onProgressChange(progress);
	}
}
