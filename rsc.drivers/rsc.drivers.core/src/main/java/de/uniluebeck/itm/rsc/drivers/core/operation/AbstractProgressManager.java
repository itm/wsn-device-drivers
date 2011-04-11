package de.uniluebeck.itm.rsc.drivers.core.operation;

import com.google.common.base.Preconditions;



/**
 * The AbstractProgressManager enables to submit done work.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractProgressManager {
	
	/**
	 * Indicates that progress manager should shut down.
	 */
	private boolean done;
	
	/**
	 * The amount of work that has been done.
	 */
	private float progress = 0.0f;
	
	/**
	 * Creates a child progress manager that manages the given amount.
	 * 
	 * @param amount The amount of progress the manager is allowed to handle.
	 * @return The child progress manager for the given amount of progress.
	 */
	public abstract AbstractProgressManager createSub(final float amount);
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	public void worked(final float amount) {
		Preconditions.checkArgument(amount >= 0.0f, "Amount can not be negative");
		final float result = progress + amount <= 1.0f ? amount : 1.0f - progress;
		if (!isDone() && progress < progress + result) {
			progress += result;
			onWorked(progress, result);
		}
	}
	
	/**
	 * Method is called when the worked method was successfully applied.
	 * 
	 * @param progress The current new progress.
	 * @param worked The work that was added.
	 */
	protected void onWorked(final float progress, final float worked) {
		
	}
	
	/**
	 * Set the worked amount fo 1.0f.
	 * This method should always be called when a progress manager is not longer needed.
	 */
	public void done() {
		worked(1.0f);
		done = true;
	}
	
	/**
	 * Returns true when the progress manager is done.
	 * 
	 * @return A boolean.
	 */
	public boolean isDone() {
		return done;
	}
	
	public float getProgress() {
		return progress;
	}
}
