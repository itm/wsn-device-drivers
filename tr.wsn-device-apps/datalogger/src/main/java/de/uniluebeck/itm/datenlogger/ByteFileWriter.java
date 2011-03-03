package de.uniluebeck.itm.datenlogger;

import java.io.FileOutputStream;
import java.io.IOException;

public class ByteFileWriter extends AbstractFileWriter {

	FileOutputStream writer;

	@Override
	public void write(byte[] content, int messageType) {
		try {
			String temp = "";
			for (byte a : content) {
				temp += a + " ";
			}
			temp += "\n";
			writer.write(temp.getBytes());
		} catch (IOException e) {
			System.out.println("Cannot write to file.");
		}
	}

	@Override
	public void setLocation(String location) {
		try {
			this.writer = new FileOutputStream(location);
		} catch (IOException e) {
			System.out.println("FileOutputStream could not be created.");
		}
	}

	@Override
	public String convert(byte[] content) {
		return "";
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}