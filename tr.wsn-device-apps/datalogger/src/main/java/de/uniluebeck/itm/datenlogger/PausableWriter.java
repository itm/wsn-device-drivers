package de.uniluebeck.itm.datenlogger;

import java.io.Closeable;

public interface PausableWriter extends Closeable {

	void write(byte[] content);
	
	void pause();
	
	void resume();
}
