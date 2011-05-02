package de.uniluebeck.itm.rsc.drivers.core.operation;

import com.google.common.base.Preconditions;


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
