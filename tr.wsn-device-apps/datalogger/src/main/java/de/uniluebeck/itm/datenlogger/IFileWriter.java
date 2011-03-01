package de.uniluebeck.itm.datenlogger;

import java.io.IOException;
import java.util.regex.Pattern;

public interface IFileWriter{
	
	public void write(byte[] content);
	
	public void setPattern(Pattern pattern);
	
	public void close() throws IOException;
	
	public String convert(byte[] content);
}
