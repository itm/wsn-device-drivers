package de.uniluebeck.itm.datenlogger;

import java.io.IOException;
import java.util.regex.Pattern;

public abstract class AbstractConsoleWriter implements PausableWriter, PatternAware, BracketAware {

	private boolean isPaused = false;
	
	private Pattern pattern = Pattern.compile(".*");
	private String bracketFilter = "";
	
	@Override
	public void write(byte[] content) {
		final String output = convert(content);
		//if (!isPaused && pattern.matcher(output).matches() && parse_klammer_filter(bracketFilter).apply(output)) {
			System.out.println(output);
		//}
	}

	@Override
	public void pause() {
		isPaused = true;
	}

	@Override
	public void resume() {
		isPaused = false;
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
		
	}
	
	public abstract String convert(byte[] content);
}
