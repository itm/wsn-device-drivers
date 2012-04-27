package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.spot.client.ui.SunspotCommandUI;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.UUID;

public class SunspotProgramOperation extends TimeLimitedOperation<Void> implements ProgramOperation {

	private static final Logger log = LoggerFactory.getLogger(SunspotProgramOperation.class);

	private final byte[] image;

	private final String macAddress;

	private final String sysBinPath;

	private final String libFilePath;

	private final String keyStorePath;

	private final String port;

	private final String iPort;

	private final String workingDirectory;

	@Inject
	public SunspotProgramOperation(final TimeLimiter timeLimiter,
								   @Assisted final long timeoutMillis,
								   @Assisted @Nullable final OperationListener<Void> operationListener,
								   final byte[] image, final String macAddress, final String sysBinPath,
								   final String libFilePath, final String keyStorePath, final String port,
								   final String iPort, final String workingDirectory) {
		super(timeLimiter, timeoutMillis, operationListener);
		this.image = image;
		this.macAddress = macAddress;
		this.sysBinPath = sysBinPath;
		this.libFilePath = libFilePath;
		this.keyStorePath = keyStorePath;
		this.port = port;
		this.iPort = iPort;
		this.workingDirectory = workingDirectory;
	}

	@Override
	protected Void callInternal() throws Exception {
		String workindDir = explodeJar();
		final SunspotCommandUI ss = new SunspotCommandUI();
		try {
			log.debug("DEPLOY APP NODE:" + this.macAddress);
			String[] args = new String[7];
			args[0] = this.sysBinPath;
			args[1] = this.libFilePath;
			args[2] = this.keyStorePath;
			args[3] = this.port;
			args[4] = this.iPort;
			args[5] = "-remote.address=" + this.macAddress;
			args[6] = "-scriptString=flashapp true 1 " + workindDir + ":quit";
			ss.initialize(args);
		} catch (Exception e) {
			log.error("DEPLOY APP ERROR:" + this.macAddress + ": " + e.getMessage());
			deleteFile(workindDir);
			throw new Exception(e.getMessage());
		}
		log.debug("SUNSPOT DEPLOY APP OK:" + this.macAddress);
		deleteFile(workindDir);
		return null;
	}

	private String explodeJar() throws IOException {
		String working_dir = this.workingDirectory + File.separatorChar + UUID.randomUUID() + java.io.File.separator;
		boolean dir1 = (new File(working_dir)).mkdirs();
		String jar_fpath = working_dir + "program.jar";
		File jarfile = new File(jar_fpath);
		OutputStream outf = new FileOutputStream(jarfile);
		outf.write(this.image);
		outf.flush();
		outf.close();
		jar_fpath = jarfile.getAbsolutePath();
		try {
			unJarFile(jar_fpath, working_dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return working_dir;
	}

	public static boolean deleteFile(String sFilePath) {
		File oFile = new File(sFilePath);
		if (oFile.isDirectory()) {
			File[] aFiles = oFile.listFiles();
			for (File oFileCur : aFiles) {
				deleteFile(oFileCur.getAbsolutePath());
			}
		}
		return oFile.delete();
	}

	public void unJarFile(String jarFilePath, String targetDirectoryPath) throws Exception {
		InputStream jarFileInputStream = null;
		try {
			File jarFile = new File(jarFilePath);
			jarFileInputStream = new FileInputStream(jarFile);
			JarArchiveInputStream jarInput = new JarArchiveInputStream(jarFileInputStream);
			while (true) {
				JarArchiveEntry entry = jarInput.getNextJarEntry();
				if (entry == null) {
					break;
				}
				File file = new File(targetDirectoryPath + File.separator + entry.getName());
				if (entry.isDirectory()) {
					file.mkdir();
					continue;
				}
				OutputStream jarFileOutputStream = null;
				try {
					jarFileOutputStream = new FileOutputStream(new File(targetDirectoryPath, entry.getName()));
					IOUtils.copy(jarInput, jarFileOutputStream);
				} finally {
					if (jarFileOutputStream != null) {
						jarFileOutputStream.close();
					}
				}

			}
		} finally {
			if (jarFileInputStream != null) {
				jarFileInputStream.close();
			}
		}
	}
}
