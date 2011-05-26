package de.uniluebeck.itm.wsn.drivers.core;

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
	 * The <code>Operation</code> reached the timeout.
	 */
	TIMEDOUT("Timedout"),
	
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
	private State(final String name) {
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
	
	/**
	 * Returns true when this state is equals to DONE, EXCEPTED or CANCELED else false.
	 * 
	 * @return A boolean for representing a finished state.
	 */
	public static boolean isFinishState(final State state) {
		return state != null && (state == State.DONE || state == State.EXCEPTED || state == State.CANCELED || state == State.TIMEDOUT);
	}
	
	/**
	 * Determine the State by his name.
	 * 
	 * @param name The name of the state that has to be retuned.
	 * @return The state with the given name or null.
	 */
	public static State fromName(final String name) {
		for (State state : State.values()) {
			if (state.getName().equals(name)) {
				return state;
			}
		}
		throw new IllegalArgumentException("No constant with name " + name + " found");
	}
	
	/**
	 * Determine the State by his String representation.
	 * 
	 * @param value The string representing the State.
	 * @return The appropriate String.
	 */
	public static State fromString(final String value) {
		return State.valueOf(value);
	}
}