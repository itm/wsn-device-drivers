package de.uniluebeck.itm.datenlogger;

import java.io.Serializable;

import com.google.common.base.Predicate;

/**
 * The Class Brackets_Predicate.
 * Represents an object for the (Datatype, Begin, Value)-Filter
 */
public class Brackets_Predicate implements Predicate<CharSequence>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 775543062421891927L;
	
	/** The filter given by the user. */
	private String filter;

	/**
	 * Instantiates a new brackets_ predicate.
	 *
	 * @param filter, given by the user.
	 */
	public Brackets_Predicate(String filter) {
		this.filter = filter;
		filter = filter.substring(1, filter.length() - 1);	//Deletes the brackets
	}

	/*
	 * Method to match the incomming data with the given filter
	 * @return true, if the filter machtes, else false
	 * @see com.google.common.base.Predicate#apply(java.lang.Object)
	 */
	public boolean apply(CharSequence incoming_data) {
		boolean result = true;
		String[] single_filter = filter.split(",");
		// match Datatype
		if (!single_filter[0].equals(incoming_data.subSequence(0, 6))) {
			result = false;
		}
		// match Value
		int begin = Integer.parseInt(single_filter[1]);
		if (single_filter[2].charAt(0) != incoming_data.charAt(begin + 6)) {
			result = false;
		}
		return result;
	}

}
