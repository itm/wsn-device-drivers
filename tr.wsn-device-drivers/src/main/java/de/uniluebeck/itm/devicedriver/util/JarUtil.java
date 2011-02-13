package de.uniluebeck.itm.devicedriver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


/**
 * Utility class for JAR handling.
 * 
 * @author Malte Legenhausen
 */
public class JarUtil {
	
	/**
	 * Buffer size for 
	 */
	private static final int BUFFER_SIZE = 1048;

	/**
	 * Load a DLL or SO file that is contained in a JAR.
	 * This method is designed to work like System.loadLibrary(libName). 
	 * 
	 * @param libName The name of the libary without fileextension.
	 */
	public static void loadLibrary(final String libName) {
		final String system = System.getProperty("os.name");
		final String libExtension = system.startsWith("Windows") ? ".dll" : ".so";
		final String lib = libName + libExtension;
		final URL libUrl = ClassLoader.getSystemResource(lib);
		final File file = new File(lib);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			final FileInputStream in = new FileInputStream(libUrl.getFile());
			final FileOutputStream out = new FileOutputStream(file);
			final byte[] buffer = new byte[BUFFER_SIZE];
			while(in.available() > 0) {
			   final int read = in.read(buffer);
			   out.write(buffer, 0, read);
			}
			out.close();
		} catch(final IOException e) {
			throw new RuntimeException(e);
		}
		System.loadLibrary(libName);
	}
}
