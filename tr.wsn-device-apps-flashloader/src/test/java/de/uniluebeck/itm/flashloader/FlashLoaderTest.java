package de.uniluebeck.itm.flashloader;

import de.uniluebeck.itm.devicedriver.MacAddress;
import junit.framework.TestCase;

public class FlashLoaderTest extends TestCase {
	
	public void testSetPort(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.setPort("123");
	    assertEquals("123", flashloader.port);
	}
	
	public void testSetServer(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.setServer("123");
	    assertEquals("123", flashloader.server);
	}
	
	public void testFlash(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.flash("123");
		assertEquals(true, flashloader.geflasht);
	}
	
	public void testWriteReadMac(){
		FlashLoader flashloader = new FlashLoader();
		MacAddress macAdresse = new MacAddress(1024);
		flashloader.writemac(macAdresse);
		flashloader.readmac();
		assertEquals(macAdresse, flashloader.current_mac_adress);
	}
	
	public void testReset(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.reset();
		assertEquals(true, flashloader.geresetet);
	}
}
