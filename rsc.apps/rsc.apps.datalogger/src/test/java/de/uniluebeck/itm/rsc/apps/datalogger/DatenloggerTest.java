package de.uniluebeck.itm.rsc.apps.datalogger;

import org.junit.Test;


public class DatenloggerTest {
	
	@Test
	public void emptyTest() {
		
	}
	
	/*
	@Test
	public void testStartlog(){
		Datalogger logger = new Datalogger();
		logger.connect();
		logger.startlog();
		assertEquals(true, logger.isStarted());
		logger.stoplog();
	}
	
	@Test
	public void testStoplog(){
		Datalogger logger = new Datalogger();
		logger.connect();
		logger.startlog();
		logger.stoplog();
		assertEquals(false, logger.isStarted());
	}
	
	@Test
	public void testAdd_klammer_filter(){
		Datalogger logger = new Datalogger();
		logger.add_klammer_filter("(uint32,6,28)");
		logger.add_klammer_filter("&(uint32,5,17)|(int16,0,3)");
		assertEquals("(uint32,6,28)&(uint32,5,17)|(int16,0,3)", logger.getKlammer_filter());
	}
	
	@Test
	public void testAdd_regex_filter(){
		Datalogger logger = new Datalogger();
		logger.add_regex_filter("(a&b)");
		logger.add_regex_filter("|c");
		assertEquals("(a&b)|c", logger.getRegex_filter());
	}
	*/
}

