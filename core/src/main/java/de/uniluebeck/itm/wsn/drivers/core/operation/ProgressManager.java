package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * The ProgressManager enables to submit done work.
 * 
 * @author Malte Legenhausen
 */
public interface ProgressManager {
	
	/**
	 * Constant value for 50% worked.
	 */
	float WORKED_HALF = 0.5f;
	
	/**
	 * Constant value for 25% worked.
	 */
	float WORKED_QUARTER = 0.25f;
	
	/**
	 * Creates a child progress manager that manages the given amount.
	 * 
	 * @param amount The amount of progress the manager is allowed to handle.
	 * @return The child progress manager for the given amount of progress.
	 */
	ProgressManager createSub(final float amount);
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	void worked(final float amount);
	
	/**
	 * Set the worked amount of 1.0f.
	 * This method should always be called when a progress manager is not longer needed.
	 */
	void done();
	
	/**
	 * Returns true when the progress manager is done.
	 * 
	 * @return A boolean.
	 */
	boolean isDone();
	
	/**
	 * Getter for the current progress.
	 * 
	 * @return The current progress from 0.0f till 1.0f.
	 */
	float getProgress();
}
