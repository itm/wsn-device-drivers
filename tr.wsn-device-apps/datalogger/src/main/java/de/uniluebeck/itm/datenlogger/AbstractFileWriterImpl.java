package de.uniluebeck.itm.datenlogger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public abstract class AbstractFileWriterImpl implements IFileWriter, PatternAware, BracketAware {

	private FileWriter writer;
	private String location;
	private Pattern pattern = Pattern.compile(".*");
	private String bracketFilter = "";
	
	@Override
	public void write(byte[] content) {
		final String output = new String(content).substring(1);
		//if (pattern.matcher(output).matches() && parse_klammer_filter(bracketFilter).apply(output)) {
			try {
				writer.write(output);
			} catch (IOException e) {
				System.out.println("Cannot write to file.");
			}
		//}
	}

	@Override
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public void setBracketFilter(String bracketFilter) {
		this.bracketFilter = bracketFilter;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
	
	@Override
	public abstract String convert(byte[] content);
}
