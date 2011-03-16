package de.uniluebeck.itm.datenlogger;

import java.io.Closeable;

/**
 * The Interface PausableWriter.
 */
public interface PausableWriter extends Closeable {

	/**
	 * Writes the content to the current output location.
	 * @param content
	 *            the content
	 * @param messageType
	 *            the message type
	 */
	void write(final byte[] content, final int messageType);

	/**
	 * Writer gets paused.
	 */
	void pause();

	/**
	 * Writer gets resumed.
	 */
	void resume();

	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	void setLocation(final String location);

	/**
	 * Sets the bracket filter.
	 *
	 * @param bracketFilter the new bracket filter
	 */
	void setBracketFilter(final String bracketFilter);

	/**
	 * Sets the regex filter.
	 *
	 * @param regexFilter the new regex filter
	 */
	void setRegexFilter(final String regexFilter);

	/**
	 * Adds the bracket filter.
	 *
	 * @param bracketFilter the bracket filter
	 */
	void addBracketFilter(final String bracketFilter);

	/**
	 * Adds the regex filter.
	 *
	 * @param regexFilter the regex filter
	 */
	void addRegexFilter(final String regexFilter);

	/**
	 * Gets the regex filter.
	 * @return the regex filter
	 */
	String getRegexFilter();

	/**
	 * Gets the bracket filter.
	 * @return the bracket filter
	 */
	String getBracketFilter();
}
