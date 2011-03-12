package de.uniluebeck.itm.datenlogger;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The Class ByteFileWriter.
 */
public class ByteFileWriter extends AbstractFileWriter {

	FileOutputStream writer;

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#write(byte[], int)
	 */
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

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#setLocation(java.lang.String)
	 */
	@Override
	public void setLocation(String location) {
		try {
			this.writer = new FileOutputStream(location);
		} catch (IOException e) {
			System.out.println("FileOutputStream could not be created.");
		}
	}

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#convert(byte[])
	 */
	@Override
	public String convert(byte[] content) {
		return "";
	}

	/* 
	 * @see de.uniluebeck.itm.datenlogger.AbstractFileWriter#close()
	 */
	@Override
	public void close() throws IOException {
		writer.close();
	}

}