package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.Adler32;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;


/**
 * Utility class for JAR handling.
 * 
 * @author Malte Legenhausen
 */
public class JarUtil {

	/**
	 * Path to the root directory of the jni libary files.
	 */
	private static final String JNI_PATH_ROOT = "/de/uniluebeck/itm/rsc/drivers/core/jni";
	
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
			throw new RuntimeException("Unable to extract libary to: " + path, e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("Unable to extract libary to: " + path, e);
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
		final String pattern = system.startsWith("Windows") ? "%s.dll" : "lib%s.so";
		return String.format(pattern, libName);
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
	 * @param lib The destinated library name.
	 * @throws IOException When a file operation during the extraction failed.
	 * @throws URISyntaxException When the classloader url can not be converted to a uri.
	 */
	private static void extractLibrary(final String path, final String lib) throws IOException, URISyntaxException {
		final InputStream stream = JarUtil.class.getResourceAsStream(path);
		if (stream == null) {
			throw new IOException("Unable to find library on classpath: " + path);
		}
		
		// Initialize the target file and create if necessary.
		final File target = new File(lib);
		if (!target.exists()) {
			target.createNewFile();
		}

		final File temp = readStreamToTemp(stream);
		
		// Only copy the source to target when the file has changed.
		if (hasFileChanged(temp, target)) {
			Files.copy(temp, target);
		}
		// Remove the temp file when the program is closed.
		temp.deleteOnExit();
	}
	
	/**
	 * Reads all data from the given stream and save it to a temp file.
	 * 
	 * @param stream The stream with the data that has to be read.
	 * @return The temp file object.
	 * @throws IOException When something happend during the file operations.
	 */
	private static File readStreamToTemp(final InputStream stream) throws IOException {
		final File tempDir = Files.createTempDir();
		final File temp = new File(tempDir, "tmplib" + String.valueOf(System.currentTimeMillis()));
		temp.createNewFile();
		
		final InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {
			@Override
			public InputStream getInput() throws IOException {
				return stream;
			}
		};
		Files.copy(supplier, temp);
		return temp;
	}
	
	/**
	 * Check via Adler32 if a given targte file differs from the given source.
	 * 
	 * @param source The source file.
	 * @param target The target file.
	 * @return True when the files are not equal, so they changed, else false.
	 * @throws IOException when something happened current the checksum calculation.
	 */
	private static final boolean hasFileChanged(final File source, final File target) throws IOException {
		return Files.getChecksum(source, new Adler32()) != Files.getChecksum(target, new Adler32());
	}
}
