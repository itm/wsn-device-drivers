package de.uniluebeck.itm.rsc.drivers.core.operation;

import com.google.common.base.Preconditions;

/**
 * Child progress manager that should only be created by the RootProgressManager.
 * 
 * @author Malte Legenhausen
 */
public class ChildProgressManager extends AbstractProgressManager<ChildProgressManager> {

	/**
	 * A parent progress manager. This is set when this progress manager is running in a sub operation.
	 */
	private final AbstractProgressManager<?> parent;
	
	/**
	 * The factor is used to limit the amount of work.
	 */
	private final float factor;
	
	/**
	 * Private constructor.
	 * 
	 * @param parent The parent of this progress manager.
	 * @param factor The amount of progress the manager can manage.
	 */
	public ChildProgressManager(final AbstractProgressManager<?> parent, final float factor) {
		this.factor = factor;
		this.parent = parent;
	}
	
	/**
	 * Method for creating a sub AbstractProgressManager.
	 * 
	 * @param amount The amount of progress the sub AbstractProgressManager is allowed to handle.
	 * @return The new sub AbstractProgressManager.
	 */
	@Override
	public ChildProgressManager createSub(final float amount) {
		return new ChildProgressManager(this, amount);
	}
	
	/**
	 * Use this method to raise the amount of work that was already done.
	 * The amount of work starts at 0.0f and will be raised by the amount that is given.
	 * 
	 * @param amount The worked amount.
	 */
	@Override
	public void worked(final float amount) {
		Preconditions.checkArgument(amount >= 0.0f, "Amount can not be negative");
		parent.worked(amount * factor);
	}
}
