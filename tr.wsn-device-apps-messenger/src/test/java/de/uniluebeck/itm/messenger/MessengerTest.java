package de.uniluebeck.itm.messenger;

import junit.framework.TestCase;

public class MessengerTest extends TestCase {

	public void testSetPort(){
		Messenger messenger = new Messenger();
	    messenger.setPort("123");
	    assertEquals("123", messenger.port);
	}
	
	public void testSetServer(){
		Messenger messenger = new Messenger();
	    messenger.setServer("123");
	    assertEquals("123", messenger.server);
	}
	
	public void testSend(){
		Messenger messenger = new Messenger();
	    messenger.send("123");
	    assertEquals(true, messenger.gesendet);
	}
}
