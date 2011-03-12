package de.uniluebeck.itm.tcp.Server;

import java.io.IOException;

import de.uniluebeck.itm.tcp.server.Main;

/**
 * Class to test the Server
 * @author Andreas Maier
 *
 */
public class ServerTest {

	/**
	 * starts the Server
	 * @param args null
	 */
	public static void main(final String[] args) {

		try {
			// test the Server with default-configs
			Main.main(new String[]{});
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
