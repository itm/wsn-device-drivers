package de.uniluebeck.itm.devicedriver;

/**
 * Enum for all states of an <code>Operation</code>.
 * 
 * @author Malte Legenhausen
 */
public enum State {
	
	/**
	 * The <code>Operation</code> is waiting in the execution queue.
	 */
	WAITING("Waiting"),
	
	/**
	 * The <code>Operation</code> is currently running.
	 */
	RUNNING("Running"),
	
	/**
	 * The <code>Operation</code> has been canceled.
	 */
	CANCELED("Canceled"),
	
	/**
	 * The <code>Operation</code> finsihed with an exception.
	 */
	EXCEPTED("Excepted"),
	
	/**
	 * The <code>Operation</code> is done.
	 */
	DONE("Done");
	
	/**
	 * The name of the state.
	 */
	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name The name of the state.
	 */
	private State(String name) {
		this.name = name;
	}
	
	/**
	 * Getter for the state name.
	 * 
	 * @return The name as <code>String</code>.
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}