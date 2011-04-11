package de.uniluebeck.itm.rsc.drivers.core.operation;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;


/**
 * The root progress manager.
 * 
 * @author Malte Legenhausen
 */
public class RootProgressManager extends AbstractProgressManager {
	
	/**
	 * The monitor instance that is used for response.
	 */
	private final Monitor monitor;
	
	/**
	 * Constructor.
	 * 
	 * @param monitor Monitor instance.
	 */
	public RootProgressManager(final Monitor monitor) {
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
