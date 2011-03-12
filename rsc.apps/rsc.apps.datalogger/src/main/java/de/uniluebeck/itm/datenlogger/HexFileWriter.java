package de.uniluebeck.itm.datenlogger;

import de.uniluebeck.itm.tr.util.StringUtils;

/**
 * The Class HexFileWriter.
 */
public class HexFileWriter extends AbstractFileWriter{

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#convert(byte[])
	 */
	@Override
	public String convert(byte[] content) {
		return StringUtils.toHexString(content);
	}

}
