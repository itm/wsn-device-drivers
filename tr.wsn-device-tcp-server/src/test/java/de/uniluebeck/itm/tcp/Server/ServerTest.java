package de.uniluebeck.itm.tcp.Server;

import de.uniluebeck.itm.tcp.server.Server;

public class ServerTest {

	public static void main(String[] args) {
		
		Server server = new Server("localhost",8080);
		server.start();

	}

}
