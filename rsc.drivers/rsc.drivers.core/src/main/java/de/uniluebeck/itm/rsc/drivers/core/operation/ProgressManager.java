package de.uniluebeck.itm.rsc.drivers.core.operation;

import com.google.common.base.Preconditions;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;


/**
 * The ProgressManager enables to submit done work.
 * 
 * @author Malte Legenhausen
 */
public class ProgressManager {
	
	/**
	 * A parent progress manager. This is set when this progress manager is running in a sub operation.
	 */
	private final ProgressManager parent;
	
	/**
	 * The monitor instance that is used for response.
	 */
	private final Monitor monitor;
	
	/**
	 * The factor is used to limit the amount of work.
	 */
	private final float factor;
	
	/**
	 * The amount of work that has been done.
	 */
	private float worked = 0.0f;
	
	/**
	 * Constructor.
	 * 
	 * @param monitor Monitor instance.
	 */
	public ProgressManager(final Monitor monitor) {
		this.factor = 1.0f;
		this.monitor = monitor;
		this.parent = null;
	}
	
	/**
	 * Private constructor.
	 * 
	 * @param parent The parent of this progress manager.
	 * @param factor The amount of progress the manager can manage.
	 */
	private ProgressManager(final ProgressManager parent, final float factor) {
		this.factor = factor;
		this.parent = parent;
		this.monitor = null;
	}
	
	/**
	 * Method for creating a sub ProgressManager.
	 * 
	 * @param amount The amount of progress the sub ProgressManager is allowed to handle.
	 * @return The new sub ProgressManager.
	 */
	public ProgressManager createSub(final float amount) {
		return new ProgressManager(this, amount);
	}
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	public void worked(final float amount) {
		Preconditions.checkArgument(amount >= 0.0f, "Amount can not be negative");
		final float result = amount * factor;
		if (monitor != null) {
			worked = worked + result <= 1.0f ? worked + result : 1.0f;
			monitor.onProgressChange(worked);
		} else if(parent != null) {
			parent.worked(result);
		}
	}
	
	/**
	 * Set the worked amount fo 1.0f.
	 */
	public void done() {
		worked(1.0f);
	}
}
