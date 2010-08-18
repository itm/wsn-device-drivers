package de.uniluebeck.itm.devicedriver;

public interface ConnectionListener {

	void beforeConnectionChange(Connection connection, boolean connected);
	
	void afterConnectionChange(Connection connection, boolean connected);
}
