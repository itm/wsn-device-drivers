package de.uniluebeck.itm.rsc.drivers.core.operation;



/**
 * The AbstractProgressManager enables to submit done work.
 * 
 * @author Malte Legenhausen
 * @param <T> Type of the child.
 */
public abstract class AbstractProgressManager <T extends AbstractProgressManager<?>> {
	
	/**
	 * Creates a child progress manager that manages the given amount.
	 * 
	 * @param amount The amount of progress the manager is allowed to handle.
	 * @return The child progress manager for the given amount of progress.
	 */
	public abstract T createSub(final float amount);
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	public abstract void worked(final float amount);
	
	/**
	 * Set the worked amount fo 1.0f.
	 * This method should always be called when a progress manager is not longer needed.
	 */
	public void done() {
		worked(1.0f);
	}
}
