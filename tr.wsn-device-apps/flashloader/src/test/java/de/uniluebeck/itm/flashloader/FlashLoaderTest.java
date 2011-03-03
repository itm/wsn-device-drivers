package de.uniluebeck.itm.flashloader;

import de.uniluebeck.itm.devicedriver.MacAddress;
import junit.framework.TestCase;

public class FlashLoaderTest extends TestCase {
	
	/**
	
	public void testSetPort(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.setPort("123");
	    assertEquals("123", flashloader.getPort());
	}
	
	public void testSetServer(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.setServer("123");
	    assertEquals("123", flashloader.getServer());
	}
	
	public void testFlash(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.connect();
		flashloader.flash("123");
		try {
	    	Thread.sleep(10000);
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    }
		assertEquals(true, flashloader.isFlashed());
	}
	
	public void testWriteReadMac(){
		FlashLoader flashloader = new FlashLoader();
		MacAddress macAdresse = new MacAddress("0x0 0x0 0x0 0x0 0x0 0x0 0x4 0x0".getBytes());
		flashloader.connect();
		flashloader.writemac(macAdresse);
		try {
	    	Thread.sleep(10000);
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    }
		flashloader.readmac();
		try {
	    	Thread.sleep(10000);
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    }
		assertEquals(macAdresse, flashloader.getCurrent_mac_adress());
	}
	
	public void testReset(){
		FlashLoader flashloader = new FlashLoader();
		flashloader.connect();
		flashloader.reset();
		try {
	    	Thread.sleep(10000);
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    }
		assertEquals(true, flashloader.isResetet());
	}
	
	*/
}
