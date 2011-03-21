package de.uniluebeck.itm.rsc.apps.datalogger;

/**
 * The Class StringFileWriter. Overrides the convert-method, to write the
 * content as String to a file.
 */
public class StringFileWriter extends AbstractFileWriter {

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.AbstractFileWriter#convert(byte[])
	 */
	@Override
	public String convert(final byte[] content) {
		return new String(content).substring(1);
	}

}
