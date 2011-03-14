package de.uniluebeck.itm.datenlogger;

/**
 * The Class StringFileWriter.
 * Overrides the convert-method, to write the content as String to a file.
 */
public class StringFileWriter extends AbstractFileWriter{
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#convert(byte[])
	 */
	@Override
	public String convert(byte[] content) {
		return new String(content).substring(1);
	}

}
