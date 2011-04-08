package de.uniluebeck.itm.rsc.drivers.core.operation;

import com.google.common.base.Preconditions;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;


/**
 * The root progress manager.
 * 
 * @author Malte Legenhausen
 */
public class RootProgressManager extends AbstractProgressManager<ChildProgressManager> {
	
	/**
	 * The monitor instance that is used for response.
	 */
	private final Monitor monitor;
	
	/**
	 * The amount of work that has been done.
	 */
	private float worked = 0.0f;
	
	/**
	 * Constructor.
	 * 
	 * @param monitor Monitor instance.
	 */
	public RootProgressManager(final Monitor monitor) {
		this.monitor = monitor;
	}
	
	/**
	 * Method for creating a sub AbstractProgressManager.
	 * 
	 * @param amount The amount of progress the sub AbstractProgressManager is allowed to handle.
	 * @return The new sub AbstractProgressManager.
	 */
	public ChildProgressManager createSub(final float amount) {
		return new ChildProgressManager(this, amount);
	}
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	public void worked(final float amount) {
		Preconditions.checkArgument(amount >= 0.0f, "Amount can not be negative");
		worked = worked + amount <= 1.0f ? worked + amount : 1.0f;
		monitor.onProgressChange(worked);
	}
}
