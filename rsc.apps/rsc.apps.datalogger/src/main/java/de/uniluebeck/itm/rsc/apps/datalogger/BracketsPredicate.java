package de.uniluebeck.itm.rsc.apps.datalogger;

import java.io.Serializable;

import com.google.common.base.Predicate;

/**
 * The Class Brackets_Predicate. Represents an object for the (Datatype, Begin,
 * Value)-Filter
 */
public class BracketsPredicate implements Predicate<CharSequence>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 775543062421891927L;

	/** The filter given by the user. */
	private String filter;
	
	private int messageType;

	/**
	 * Instantiates a new brackets_ predicate.
	 *
	 * @param filter , given by the user.
	 * @param messageType the message type
	 */
	public BracketsPredicate(final String filter, final int messageType) {
		this.filter = filter;
		this.messageType = messageType;
		this.filter = filter;
	}

	/**
	 * Method to match the incomming data with the given filter.
	 *
	 * @param incomingData the incoming data
	 * @return true, if the filter machtes, else false
	 * @see com.google.common.base.Predicate#apply(java.lang.Object)
	 */
	public boolean apply(final CharSequence incomingData) {
		boolean result = true;
		String[] singleFilter = filter.split(",");
		// match Datatype
		if (!singleFilter[0].equals(String.valueOf(messageType))) {
			result = false;
		}
		// match Value
		int begin = Integer.parseInt(singleFilter[1]);
		if (singleFilter[2].charAt(0) != incomingData.charAt(begin)) {
			result = false;
		}
		return result;
	}

}
