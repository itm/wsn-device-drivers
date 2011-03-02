package de.uniluebeck.itm.datenlogger;

import de.uniluebeck.itm.tr.util.StringUtils;

public class HexFileWriter extends AbstractFileWriter{

	@Override
	public String convert(byte[] content) {
		return StringUtils.toHexString(content);
	}

}
