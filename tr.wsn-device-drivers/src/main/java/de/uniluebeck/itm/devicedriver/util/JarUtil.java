package de.uniluebeck.itm.devicedriver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class JarUtil {

	public static void loadLibrary(String libName) {
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
			final byte[] buffer = new byte[1048];
			while(in.available() > 0) {
			   final int read = in.read(buffer);
			   out.write(buffer, 0, read);
			}
			out.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		System.loadLibrary(libName);
	}
}
