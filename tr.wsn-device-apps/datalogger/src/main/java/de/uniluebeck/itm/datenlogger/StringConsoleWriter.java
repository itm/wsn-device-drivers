package de.uniluebeck.itm.datenlogger;

/**
 * The Class StringConsoleWriter.
 */
public class StringConsoleWriter extends AbstractConsoleWriter {

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractConsoleWriter#convert(byte[])
	 */
	@Override
	public String convert(byte[] content) {
		return new String(content).substring(1);
	}

}
