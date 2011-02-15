package de.uniluebeck.itm.datenlogger;

import junit.framework.TestCase;

public class DatenloggerTest extends TestCase {

	public void testSetPort(){
		Datenlogger logger = new Datenlogger();
		logger.setPort("123");
	    assertEquals("123", logger.port);
	}
	
	public void testSetServer(){
		Datenlogger logger = new Datenlogger();
		logger.setServer("123");
	    assertEquals("123", logger.server);
	}
	
	public void testSetGestartet(){
		Datenlogger logger = new Datenlogger();
		logger.setStartet(true);
	    assertEquals(logger.started, true);
	}
	
	public void testSetKlammerFilter(){
		Datenlogger logger = new Datenlogger();
		logger.setKlammer_filter("(uint32,14,2)");
	    assertEquals(logger.klammer_filter, "(uint32,14,2)");
	}
	
	public void testSetRegexFilter(){
		Datenlogger logger = new Datenlogger();
		logger.setRegex_filter("a&b|c");
	    assertEquals(logger.regex_filter, "a&b|c");
	}
	
	public void testSetLocation(){
		Datenlogger logger = new Datenlogger();
		logger.setLocation("123");
	    assertEquals(logger.location, "123");
	}
	
	public void testGetLoggers(){
		Datenlogger logger = new Datenlogger();
		logger.getloggers();
	}
	
	public void testStartlog(){
		Datenlogger logger = new Datenlogger();
		logger.startlog();
		assertEquals("true", logger.started);
	}
	
	public void testStoplog(){
		Datenlogger logger = new Datenlogger();
		logger.startlog();
		logger.stoplog();
		assertEquals("false", logger.started);
	}
	
	public void testAdd_klammer_filter(){
		Datenlogger logger = new Datenlogger();
		logger.setKlammer_filter("(uint32,6,28)");
		logger.add_klammer_filter("&(uint32,5,17)|(int16,0,3)");
		assertEquals("(uint32,6,28)(uint32,5,17)|(int16,0,3)", logger.klammer_filter);
	}
	
	public void testAdd_regex_filter(){
		Datenlogger logger = new Datenlogger();
		logger.setRegex_filter("(a&b)");
		logger.add_regex_filter("|c");
		assertEquals("(a&b)|c", logger.regex_filter);
	}
	
	public void testWriteToXmlFile(){
		Datenlogger logger = new Datenlogger();
		logger.writeToXmlFile();
		//TODO ...
	}
}

