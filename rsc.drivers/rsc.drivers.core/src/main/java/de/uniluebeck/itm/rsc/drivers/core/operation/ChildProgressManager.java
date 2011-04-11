package de.uniluebeck.itm.rsc.drivers.core.operation;


/**
 * Child progress manager that should only be created by the RootProgressManager.
 * 
 * @author Malte Legenhausen
 */
public class ChildProgressManager extends AbstractProgressManager {

	/**
	 * A parent progress manager. This is set when this progress manager is running in a sub operation.
	 */
	private final AbstractProgressManager parent;
	
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
	public ChildProgressManager(final AbstractProgressManager parent, final float factor) {
		this.factor = factor;
		this.parent = parent;
	}
	
	@Override
	public ChildProgressManager createSub(final float amount) {
		return new ChildProgressManager(this, amount);
	}
	
	@Override
	protected void onWorked(final float progress, final float worked) {
		parent.worked(worked * factor);
	}
}
