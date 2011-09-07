package de.uniluebeck.itm.wsn.drivers.core.operation;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Abstract implementation of the ProgressManager.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractProgressManager implements ProgressManager {

	/**
	 * Indicates that progress manager should shut down.
	 */
	private boolean done;
	
	/**
	 * The amount of work that has been done.
	 */
	private float progress = 0.0f;
	
	@Override
	public void worked(float amount) {
		checkArgument(amount >= 0.0f, "Amount can not be negative");
		float result = amount;
		if (progress + amount > 1.0f) {
			result = 1.0f - progress;
		}
		if (!isDone() && progress < progress + result) {
			progress += result;
			onWorked(progress, result);
		}
	}
	
	/**
	 * Method is called when the worked method was successfully applied.
	 * 
	 * @param aProgress The current new progress.
	 * @param worked The work that was added.
	 */
	protected void onWorked(float aProgress, float worked) {
		
	}
	
	@Override
	public void done() {
		worked(1.0f);
		done = true;
	}
	
	@Override
	public boolean isDone() {
		return done;
	}
	
	@Override
	public float getProgress() {
		return progress;
	}
}
