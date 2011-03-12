package de.uniluebeck.itm.datenlogger;

/**
 * The Class StringFileWriter.
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
