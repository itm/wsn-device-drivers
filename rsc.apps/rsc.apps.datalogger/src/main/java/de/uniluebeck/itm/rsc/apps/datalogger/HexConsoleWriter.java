package de.uniluebeck.itm.rsc.apps.datalogger;

import de.uniluebeck.itm.tr.util.StringUtils;

/**
 * The Class HexConsoleWriter. Overrides the convert-method, to write the
 * content as hex to the console.
 */
public class HexConsoleWriter extends AbstractConsoleWriter {

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.AbstractConsoleWriter#convert(byte[])
	 */
	@Override
	public String convert(final byte[] content) {
		return StringUtils.toHexString(content);
	}

}