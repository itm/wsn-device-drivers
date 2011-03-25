package de.uniluebeck.itm.tcp.Server;

import java.io.IOException;

import org.junit.Test;

import de.uniluebeck.itm.rsc.remote.server.Main;

/**
 * Class to test the Server
 * @author Andreas Maier
 *
 */
public class ServerTest {
	
	/**
	 * 
	 */
	@Test
	public void testServer() {
		try {
			// test the Server with default-configs
			Main.main(new String[]{});
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
