package de.uniluebeck.itm.rsc.apps.datalogger;

/**
 * The Class StringConsoleWriter. Overrides the convert-method, to write the
 * content as String to the console.
 */
public class StringConsoleWriter extends AbstractConsoleWriter {

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.AbstractConsoleWriter#convert(byte[])
	 */
	@Override
	public String convert(final byte[] content) {
		return new String(content).substring(1);
	}

}
