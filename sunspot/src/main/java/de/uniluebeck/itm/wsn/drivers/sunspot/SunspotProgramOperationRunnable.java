package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.sun.spot.client.ui.SunspotCommandUI;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

public class SunspotProgramOperationRunnable implements ProgramOperation {
    private byte[] image;
    private static final Logger log = LoggerFactory.getLogger(SunspotProgramOperationRunnable.class);
    private String macAddress;
    private String sysBinPath;
    private String libFilePath;
    private String keyStrorePath;
    private String port;
    private String iport;
    private String workingDirectory;

    public SunspotProgramOperationRunnable(String macAddress, String sysBinPath, String libFilePath, String keyStrorePath, String port, String iport, byte[] suiteImage, String workingDirectory) {
        this.macAddress = macAddress;
        this.sysBinPath = sysBinPath;
        this.libFilePath = libFilePath;
        this.keyStrorePath = keyStrorePath;
        this.port = port;
        this.iport = iport;
        this.image = suiteImage;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        String workindDir = explodeJar();
        final SunspotCommandUI ss = new SunspotCommandUI();
        try {
            log.debug("DEPLOY APP NODE:" + this.macAddress);
            String[] args = new String[7];
            args[0] = this.sysBinPath;
            args[1] = this.libFilePath;
            args[2] = this.keyStrorePath;
            args[3] = this.port;
            args[4] = this.iport;
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

    @Override
    public void setBinaryImage(byte[] suiteImage) {
        this.image = suiteImage;
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
                    if (jarFileOutputStream != null)
                        jarFileOutputStream.close();
                }

            }
        } finally {
            if (jarFileInputStream != null)
                jarFileInputStream.close();
        }
    }

}
