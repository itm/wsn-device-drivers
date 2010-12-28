package de.uniluebeck.itm.entity;

import org.simpleframework.xml.Element;

public class ConfigData {
	
	@Element
	private String serverIP;
	@Element
	private int port;
	@Element
	private int clientport;
	@Element
	private String username;
	@Element
	private String password;
	@Element
	private String wisemlFile;
	
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
	public int getClientport() {
		return clientport;
	}
	public void setClientport(int clientport) {
		this.clientport = clientport;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getWisemlFile() {
		return wisemlFile;
	}
	public void setWisemlFile(String wisemlFile) {
		this.wisemlFile = wisemlFile;
	}

}
