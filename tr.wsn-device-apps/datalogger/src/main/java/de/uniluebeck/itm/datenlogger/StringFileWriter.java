package de.uniluebeck.itm.datenlogger;

public class StringFileWriter extends AbstractFileWriter{
	
	@Override
	public String convert(byte[] content) {
		return new String(content).substring(1);
	}

}
