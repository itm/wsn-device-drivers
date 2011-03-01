package de.uniluebeck.itm.datenlogger;

public class StringConsoleWriter extends AbstractConsoleWriter {

	@Override
	public String convert(byte[] content) {
		return new String(content).substring(1);
	}

}
