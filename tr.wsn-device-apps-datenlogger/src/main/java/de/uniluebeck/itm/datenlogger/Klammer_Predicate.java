package de.uniluebeck.itm.datenlogger;

import java.io.Serializable;

import com.google.common.base.Predicate;

public class Klammer_Predicate implements Predicate<CharSequence>, Serializable {

	private static final long serialVersionUID = 775543062421891927L;
	String filter;
	
	public Klammer_Predicate(String filter){
		this.filter = filter;
		filter = filter.substring(1, filter.length()-1);
	}

	public boolean apply(CharSequence incoming_data) {
		boolean result = true;
		String[] single_filter = filter.split(",");
		//matche Datentyp
		if(!single_filter[0].equals(incoming_data.subSequence(0, 6))){
			result = false;
		}
		//matche Wert
		int beginn = Integer.parseInt(single_filter[1]);
		if(single_filter[2].charAt(0) != incoming_data.charAt(beginn + 6)){
			result = false;
		}
		return result;
	}

}