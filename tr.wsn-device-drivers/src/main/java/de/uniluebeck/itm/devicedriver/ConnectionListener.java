package de.uniluebeck.itm.devicedriver;

public interface ConnectionListener {
	
	void onConnectionChange(Connection connection, boolean connected);
}
