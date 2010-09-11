package de.uniluebeck.itm.devicedriver.util;

import java.util.Date;

public class TimeDiff {

	private final long timestamp;
	
	public TimeDiff() {
		timestamp = new Date().getTime();
	}
	
	public long ms() {
		return new Date().getTime() - timestamp;
	}

}
