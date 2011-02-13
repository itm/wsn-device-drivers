package de.uniluebeck.itm.devicedriver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Utility class for file operations.
 * 
 * @author Malte Legenhausen
 */
public class FileUtil {

	/**
	 * Convert the content of a file to byte array.
	 * 
	 * @param file The file that has to be converted.
	 * @return The file content as byte array.
	 * @throws IOException when file is to big or something happend with the input stream of the file.
	 */
	public static byte[] fileToBytes(final File file) throws IOException {
		final InputStream inputStream = new FileInputStream(file);

	    // Get the size of the file
	    final long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        throw new IOException("File size to big. Max size: " + Integer.MAX_VALUE + " bytes");
	    }

	    // Create the byte array to hold the data
	    final byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file " + file.getName());
	    }

	    // Close the input stream and return bytes
	    inputStream.close();
	    
	    return bytes;
	}
}
