package de.uniluebeck.itm.devicedriver.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.base.Joiner;
import com.google.common.io.Files;


/**
 * Utility class for JAR handling.
 * 
 * @author Malte Legenhausen
 */
public class JarUtil {

	/**
	 * Path to the root directory of the jni libary files.
	 */
	private static final String JNI_PATH_ROOT = "de/uniluebeck/itm/devicedriver/jni";
	
	/**
	 * Load a DLL or SO file that is contained in a JAR.
	 * This method is designed to work like System.loadLibrary(libName). 
	 * 
	 * @param libName The name of the libary without fileextension.
	 */
	public static void loadLibrary(final String libName) {
		final String lib = nativeLibraryName(libName);
		final String path = archAwarePath(lib);
		try {
			extractLibrary(path, lib);
			System.loadLibrary(libName);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to find libary in " + path, e);
		}
	}
	
	/**
	 * Add the native file extension to the given libName dependent of the operating system.
	 * 
	 * @param libName The name of the library that has to be extended.
	 * @return The name of the library with system file extension.
	 */
	private static final String nativeLibraryName(final String libName) {
		final String system = System.getProperty("os.name");
		final String libExtension = system.startsWith("Windows") ? "dll" : "so";
		return libName + "." + libExtension;
	}
	
	/**
	 * Generate the path to the lib.
	 * The path dependents on the architecture of the system.
	 * 
	 * @param lib The native file name for the system.
	 * @return The path to the library.
	 */
	private static final String archAwarePath(final String lib) {
		final String arch = System.getProperty("os.arch");
		return Joiner.on('/').join(JNI_PATH_ROOT, arch, lib);
	}
	
	/**
	 * Extracts the library from the jar in the current working directory.
	 * 
	 * @param path The path of the libary.
	 * @param lib The destination library name.
	 * @throws IOException When a file operation during the extraction failed.
	 */
	private static final void extractLibrary(final String path, final String lib) throws IOException {
		final URL source = ClassLoader.getSystemResource(path);
		final File target = new File(lib);
		if (!target.exists()) {
			target.createNewFile();
		}
		Files.copy(new File(source.getFile()), target);
	}
}
