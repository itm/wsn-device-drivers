package de.uniluebeck.itm.messenger;

import junit.framework.TestCase;

public class MessengerTest extends TestCase {

	public void testSetPort(){
		Messenger messenger = new Messenger();
	    messenger.setPort("123");
	    assertEquals("123", messenger.getPort());
	}
	
	public void testSetServer(){
		Messenger messenger = new Messenger();
	    messenger.setServer("123");
	    assertEquals("123", messenger.getServer());
	}
	
	public void testSend(){
		Messenger messenger = new Messenger();
		messenger.connect();
	    messenger.send("123");
	    assertEquals(true, messenger.isSent());
	}
}
