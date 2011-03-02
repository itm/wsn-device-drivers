package de.uniluebeck.itm.datenlogger;

import junit.framework.TestCase;

public class DatenloggerTest extends TestCase {
	
	/**
	
	public void testStartlog(){
		Datalogger logger = new Datalogger();
		logger.connect();
		logger.startlog();
		assertEquals(true, logger.isStarted());
		logger.stoplog();
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
		logger.add_klammer_filter("(uint32,6,28)");
		logger.add_klammer_filter("&(uint32,5,17)|(int16,0,3)");
		assertEquals("(uint32,6,28)&(uint32,5,17)|(int16,0,3)", logger.getKlammer_filter());
	}
	
	public void testAdd_regex_filter(){
		Datalogger logger = new Datalogger();
		logger.add_regex_filter("(a&b)");
		logger.add_regex_filter("|c");
		assertEquals("(a&b)|c", logger.getRegex_filter());
	}
	
	*/
}

