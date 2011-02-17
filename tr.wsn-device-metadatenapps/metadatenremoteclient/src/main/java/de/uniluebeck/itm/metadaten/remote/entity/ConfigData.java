package de.uniluebeck.itm.metadaten.remote.entity;

import org.simpleframework.xml.Element;
/**
 * 
 * @author tora
 * Configdata needed for communication with MetaDataDirectory
 */
public class ConfigData {
	
	@Element
	private String serverIP;
	@Element
	private int serverport;
	@Element
	private int clientport;
	@Element
	private String username;
	@Element
	private String password;
	@Element (required=false)
	private String wisemlFile;
	
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(final String serverIP) {
		this.serverIP = serverIP;
	}
	public int getServerPort() {
		return serverport;
	}
	public void setServerPort(final int port) {
		this.serverport = port;
	}
	public int getClientport() {
		return clientport;
	}
	public void setClientport(final int clientport) {
		this.clientport = clientport;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(final String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(final String password) {
		this.password = password;
	}
	public String getWisemlFile() {
		return wisemlFile;
	}
	public void setWisemlFile(final String wisemlFile) {
		this.wisemlFile = wisemlFile;
	}
}
