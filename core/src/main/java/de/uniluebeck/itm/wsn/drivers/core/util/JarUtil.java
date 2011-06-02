package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.Adler32;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.reflect.FieldUtils;

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
	private static final String JNI_PATH_ROOT = "/de/uniluebeck/itm/wsn/drivers/core/jni";
	
	/**
	 * The directory name of the library folder in the home directory.
	 */
	private static final String LIB_HOME = SystemUtils.USER_HOME + File.separator + ".wsn-device-drivers";
	
	/**
	 * System property for the java class path.
	 */
	private static final String JAVA_LIBRARY_PATH = "java.library.path";
	
	/**
	 * Property name for library paths.
	 */
	private static final String USR_PATHS = "usr_paths";
	
	/**
	 * Load a DLL or SO file that is contained in a JAR.
	 * This method is designed to work like System.loadLibrary(libName). 
	 * 
	 * @param libName The name of the library without file extension.
	 */
	public static void loadLibrary(final String libName) {
		final String lib = nativeLibraryName(libName);
		final String path = archAwarePath(lib);
		try {
			extractLibrary(path, lib);
			prepareClassPath();
			System.loadLibrary(libName);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to extract libary to: " + path, e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("Unable to extract libary to: " + path, e);
		}
	}
	
	/**
	 * Adds the library home to the java library path.
	 */
	private static void prepareClassPath() throws IOException {
		try {
			// This enables the java.library.path to be modified at runtime
			// From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
			Object[] paths = (Object[]) FieldUtils.readDeclaredStaticField(ClassLoader.class, USR_PATHS, true);
			if (!ArrayUtils.contains(paths, LIB_HOME)) {
				paths = ArrayUtils.add(paths, LIB_HOME);
				FieldUtils.writeDeclaredStaticField(ClassLoader.class, USR_PATHS, paths, true);
			}
			System.setProperty(JAVA_LIBRARY_PATH, SystemUtils.JAVA_LIBRARY_PATH + File.pathSeparator + LIB_HOME);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		}
	}
	
	/**
	 * Add the native file extension to the given libName dependent of the operating system.
	 * 
	 * @param libName The name of the library that has to be extended.
	 * @return The name of the library with system file extension.
	 */
	private static String nativeLibraryName(final String libName) {
		String pattern = null;
		if (SystemUtils.IS_OS_WINDOWS) {
			pattern = "%s.dll";
		} else if (SystemUtils.IS_OS_MAC_OSX) {
			pattern = "lib%s.jnilib";
		} else if (SystemUtils.IS_OS_LINUX) {
			pattern = "lib%s.so";
		} else {
			throw new RuntimeException("Your operating system is not supported.");
		}
		return String.format(pattern, libName);
	}
	
	/**
	 * Generate the path to the lib.
	 * The path dependents on the architecture of the system.
	 * 
	 * @param lib The native file name for the system.
	 * @return The path to the library.
	 */
	private static String archAwarePath(final String lib) {
		return Joiner.on("/").join(JNI_PATH_ROOT, SystemUtils.OS_ARCH, lib);
	}
	
	/**
	 * Create the target file and all necessary directories.
	 * 
	 * @param lib The full library name with extensions.
	 * @return The target file.
	 * @throws IOException when something during the IO operation happens.
	 */
	private static File createTargetFile(final String lib) throws IOException {
		final String path = LIB_HOME + File.separator + lib;
		final File target = new File(path);
		if (!target.exists()) {
			Files.createParentDirs(target);
			target.createNewFile();
		}
		return target;
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
		final File target = createTargetFile(lib);
		// Read the library from the resource to a temporary file.
		final File temp = readStreamToTemp(stream);
		
		// Only copy the source to target when the file has changed.
		if (hasFileChanged(temp, target)) {
			Files.copy(temp, target);
		}
		// Remove the temporary file when the program is closed.
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
	private static boolean hasFileChanged(final File source, final File target) throws IOException {
		return Files.getChecksum(source, new Adler32()) != Files.getChecksum(target, new Adler32());
	}
}
