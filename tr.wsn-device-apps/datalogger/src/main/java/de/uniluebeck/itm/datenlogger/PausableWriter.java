package de.uniluebeck.itm.datenlogger;

import java.io.Closeable;
import java.util.regex.Pattern;


public interface PausableWriter extends Closeable{
	
	public void write(byte[] content);
	
	void pause();
	
	void resume();
	
	void setLocation(String location);
	
	void setBracketFilter(String bracketFilter);
	
	void setRegexFilter(String regexFilter);
	
	void addBracketFilter(String bracketFilter);
	
	void addRegexFilter(String regexFilter);
}
