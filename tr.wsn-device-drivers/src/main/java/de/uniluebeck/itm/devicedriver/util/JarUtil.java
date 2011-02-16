package de.uniluebeck.itm.devicedriver.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Files;


/**
 * Utility class for JAR handling.
 * 
 * @author Malte Legenhausen
 */
public class JarUtil {

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
		final URL source = ClassLoader.getSystemResource(lib);
		final File target = new File(lib);
		try {
			if (!target.exists()) {
				target.createNewFile();
			}
			Files.copy(new File(source.getFile()), target);
			System.loadLibrary(libName);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
