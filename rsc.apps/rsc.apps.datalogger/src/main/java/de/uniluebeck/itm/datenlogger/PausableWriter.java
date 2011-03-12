package de.uniluebeck.itm.datenlogger;

import java.io.Closeable;

/**
 * The Interface PausableWriter.
 */
public interface PausableWriter extends Closeable{
	
	/**
	 * Writes the content to the current output location
	 *
	 * @param content the content
	 * @param messageType the message type
	 */
	public void write(byte[] content, int messageType);
	
	/**
	 * Writer gets paused
	 */
	void pause();
	
	/**
	 * Writer gets resumed
	 */
	void resume();
	
	/**
	 * Sets the location.
	 *
	 * @param location
	 */
	void setLocation(String location);
	
	/**
	 * Sets the bracket filter.
	 *
	 * @param bracketFilter 
	 */
	void setBracketFilter(String bracketFilter);
	
	/**
	 * Sets the regex filter.
	 *
	 * @param regexFilter 
	 */
	void setRegexFilter(String regexFilter);
	
	/**
	 * Adds the bracket filter.
	 *
	 * @param bracketFilter 
	 */
	void addBracketFilter(String bracketFilter);
	
	/**
	 * Adds the regex filter.
	 *
	 * @param regexFilter 
	 */
	void addRegexFilter(String regexFilter);

	/**
	 * Gets the regex filter.
	 *
	 * @return the regex filter
	 */
	String getRegexFilter();

	/**
	 * Gets the bracket filter.
	 *
	 * @return the bracket filter
	 */
	String getBracketFilter();
}
