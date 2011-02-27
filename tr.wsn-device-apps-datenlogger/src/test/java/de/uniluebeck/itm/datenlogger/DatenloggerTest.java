package de.uniluebeck.itm.datenlogger;

import junit.framework.TestCase;

public class DatenloggerTest extends TestCase {

	public void testSetPort(){
		Datalogger logger = new Datalogger();
		logger.setPort("123");
	    assertEquals("123", logger.getPort());
	}
	
	public void testSetServer(){
		Datalogger logger = new Datalogger();
		logger.setServer("123");
	    assertEquals("123", logger.getServer());
	}
	
	public void testSetGestartet(){
		Datalogger logger = new Datalogger();
		logger.setStartet(true);
	    assertEquals(logger.isStarted(), true);
	}
	
	public void testSetKlammerFilter(){
		Datalogger logger = new Datalogger();
		logger.setKlammer_filter("(uint32,14,2)");
	    assertEquals(logger.getKlammer_filter(), "(uint32,14,2)");
	}
	
	public void testSetRegexFilter(){
		Datalogger logger = new Datalogger();
		logger.setRegex_filter("a&b|c");
	    assertEquals(logger.getRegex_filter(), "a&b|c");
	}
	
	public void testSetLocation(){
		Datalogger logger = new Datalogger();
		logger.setLocation("123");
	    assertEquals(logger.getLocation(), "123");
	}
	
	public void testStartlog(){
		Datalogger logger = new Datalogger();
		logger.connect();
		logger.startlog();
		assertEquals(true, logger.isStarted());
	}
	
	public void testStoplog(){
		Datalogger logger = new Datalogger();
		logger.connect();
		logger.startlog();
		logger.stoplog();
		assertEquals(false, logger.isStarted());
	}
	
	public void testAdd_klammer_filter(){
		Datalogger logger = new Datalogger();
		logger.setKlammer_filter("(uint32,6,28)");
		logger.add_klammer_filter("&(uint32,5,17)|(int16,0,3)");
		assertEquals("(uint32,6,28)&(uint32,5,17)|(int16,0,3)", logger.getKlammer_filter());
	}
	
	public void testAdd_regex_filter(){
		Datalogger logger = new Datalogger();
		logger.setRegex_filter("(a&b)");
		logger.add_regex_filter("|c");
		assertEquals("(a&b)|c", logger.getRegex_filter());
	}
}

