package de.uniluebeck.itm.metadaten.entities;

import org.simpleframework.xml.Element;
/**
 * Holds the config data needed by the metadataserver. Does not include the config for 
 * Apache shiro authentication supported by the server. Also database connection details are
 * configured separatly.
 * @author tora
 *
 */

public class ConfigData {

	/**
	 * IP on which the server listens for requests
	 */
	@Element
	private String serverIP;

	/**
	 * Port on which the server listens for requests
	 */
	@Element
	private int port;
	/**
	 * Nodeentries in the repository will be deleted if theri Timestamp is older
	 * than the given overagetime in ms
	 */
	@Element
	private int overagetime;
	/**
	 * delay for the starting time in ms of the timer who cleans the repository
	 * from old entries
	 */
	@Element
	private int timerdelay;
	/**
	 * Interval for the timer who cleans the repository from old entries
	 */
	@Element
	private int timerinterval;

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getOveragetime() {
		return overagetime;
	}

	public void setOveragetime(int overagetime) {
		this.overagetime = overagetime;
	}

	public int getTimerdelay() {
		return timerdelay;
	}

	public void setTimerdelay(int timerdelay) {
		this.timerdelay = timerdelay;
	}

	public int getTimerinterval() {
		return timerinterval;
	}

	public void setTimerinterval(int timerinterval) {
		this.timerinterval = timerinterval;
	}

}
