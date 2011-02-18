package de.uniluebeck.itm.tcp.Server;

import de.uniluebeck.itm.tcp.server.Server;

/**
 * Class to start manually the Server
 * @author Andreas Maier
 *
 */
public class ServerTest {

	/**
	 * starts the Server
	 * @param args null
	 */
	public static void main(final String[] args) {
		
		final Server server = new Server("localhost",8080);
		server.start();

	}

}
