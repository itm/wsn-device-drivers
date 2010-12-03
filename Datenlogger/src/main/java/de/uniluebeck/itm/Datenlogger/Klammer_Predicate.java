package de.uniluebeck.itm.Datenlogger;

import java.io.Serializable;

import com.google.common.base.Predicate;

public class Klammer_Predicate implements Predicate<CharSequence>, Serializable {

	private static final long serialVersionUID = 775543062421891927L;
	String filter;
	
	public Klammer_Predicate(String filter){
		this.filter = filter;
		filter = filter.substring(1, filter.length()-1);
	}

	public boolean apply(CharSequence erhaltene_Daten) {
		boolean ergebnis = true;
		String[] einzelne_filter = filter.split(",");
		//matche Datentyp
		if(!einzelne_filter[0].equals(erhaltene_Daten.subSequence(0, 5))){
			ergebnis = false;
		}
		//matche Wert
		int beginn = Integer.parseInt(einzelne_filter[1]);
		if(!einzelne_filter[2].equals(erhaltene_Daten.subSequence(beginn, beginn + 5))){
			ergebnis = false;
		}
		return ergebnis;
	}

}