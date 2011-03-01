package de.uniluebeck.itm.datenlogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PausableMultiplexWriter implements PausableWriter {

	private final List<PausableWriter> writers = new ArrayList<PausableWriter>();
	
	public void addWriter(PausableWriter writer) {
		writers.add(writer);
	}
	
	@Override
	public void write(byte[] content) {
		for (PausableWriter writer : writers) {
			writer.write(content);
		}
	}
	
	@Override
	public void pause() {
		for (PausableWriter writer : writers) {
			writer.pause();
		}
	}
	
	@Override
	public void resume() {
		for (PausableWriter writer : writers) {
			writer.resume();
		}
	}

	@Override
	public void close() throws IOException {
		for (PausableWriter writer : writers) {
			writer.close();
		}
	}
}
