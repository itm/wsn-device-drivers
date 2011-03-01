package de.uniluebeck.itm.datenlogger;

import de.uniluebeck.itm.tr.util.StringUtils;

public class HexFileWriter extends AbstractFileWriterImpl{

	@Override
	public String convert(byte[] content) {
		return StringUtils.toHexString(content);
	}

}
