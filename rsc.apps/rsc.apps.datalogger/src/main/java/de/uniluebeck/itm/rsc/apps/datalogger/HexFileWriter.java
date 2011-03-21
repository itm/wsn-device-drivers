package de.uniluebeck.itm.rsc.apps.datalogger;

import de.uniluebeck.itm.tr.util.StringUtils;

/**
 * The Class HexFileWriter. Overrides the convert-method, to write the content
 * as hex to a file.
 */
public class HexFileWriter extends AbstractFileWriter {

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.AbstractFileWriter#convert(byte[])
	 */
	@Override
	public String convert(final byte[] content) {
		return StringUtils.toHexString(content);
	}

}
