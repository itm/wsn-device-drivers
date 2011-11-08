/*
 * Copyright 2006-2008 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * only, as published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 16 Network Circle, Menlo
 * Park, CA 94025 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package com.sun.spot.client.command;

import com.sun.spot.client.*;
import com.sun.spot.peripheral.ConfigPage;
import com.sun.spot.peripheral.ota.ISpotAdminConstants;
import com.sun.spot.suiteconverter.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * FlashAppCommand flash an application to the SPOT.
 */
public class FlashAppCommand extends AbstractFlashCommand {
    private static final Logger log = LoggerFactory.getLogger(FlashAppCommand.class);
    public static final String NAME = "flashapp";

    /**
     * flash an application to the SPOT, and conditionally update the startup command line
     *
     * @param helper       infrastructure-supplied source of command helper operations
     * @param isMaster     "true" if this is the suite to be used for the application on next startup, "false" otherwise
     * @param midletNumber if isMaster is true, the midlet number to run on startup, ignored otherwise
     * @param appSuiteDir  "*" if the uri should be generated from the manifest, otherwise the uri to use for this suite
     * @param name         If not null will treated as a path to a .suite file WITHOUT the ".suite" extension. If null,
     *                     then the value obtained from the helper via {@link ISpotClientCommandHelper#getAppName()}
     *                     will be used. Will throw SpotClientArgumentException if the suite does not exist.
     * @return null.
     */
    public Object execute(ISpotClientCommandHelper helper, String isMaster, String midletNumber, String appSuiteDir, String name) throws SpotClientException, IOException {
        tryToEnsureSecuritySettingsMatch(helper);

        String suiteFilePath = appSuiteDir + File.separator + "suite" + File.separator + "image.suite";
        File f = new File(suiteFilePath);
        assertThat(f.exists(), "Can't find file '" + f.getAbsolutePath() + "'");

        String uri = null;
        if (uri == null) {
            Properties manifest = new Properties();
            manifest.load(new FileInputStream(getManifestFile(f)));
            uri = ConfigPage.SPOT_SUITE_PROTOCOL_NAME + "://" +
                    manifest.getProperty("MIDlet-Vendor") + "/" +
                    manifest.getProperty("MIDlet-Name") + "/" +
                    manifest.getProperty("MIDlet-Version");
            uri = uri.replace(' ', '_');
        }
        if (!uri.startsWith(ConfigPage.SPOT_SUITE_PROTOCOL_NAME + "://")) {
            throw new SpotClientFailureException("Suite id: " + uri + " is invalid");
        }
        helper.info("Using target file name: " + uri);
        int libraryHash = helper.getLibraryHash(); // do this before sending the FLASH_APP command because it sends GET_FILE_INFO_CMD
        String descriptorString = f.getParentFile().getParentFile().getCanonicalPath();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(uri);
        dos.writeUTF(descriptorString);
        helper.getAdminTarget().sendAdminCommand(ISpotAdminConstants.FLASH_APP_CMD, baos.toByteArray());
        helper.getAdminTarget().checkResponse();

        int requiredRelocationAddress = helper.getAdminTarget().getDataInputStream().readInt();
        helper.info("Relocating application suite to 0x" + Integer.toHexString(requiredRelocationAddress));
        ConfigPage configPage = helper.getConfigPage();
        File tempFile = File.createTempFile("app", ".bintemp");
        File binFilePath = new File(appSuiteDir + tempFile.getName());
        int[] memoryAddrs = new int[]{requiredRelocationAddress, ConfigPage.LIBRARY_VIRTUAL_ADDRESS, configPage.getBootstrapAddress()};
        Suite suite = new Suite();
        suite.loadFromFile(suiteFilePath, new File(helper.getSysBinPath(), "squawk.suite").getPath());
        suite.relocateMemory(memoryAddrs);
        File outputFile = new File(binFilePath.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(outputFile);
        suite.writeToStream(new DataOutputStream(fos));
        fos.close();
        if (suite.getParent().getHash() != libraryHash) {
            throw new SpotClientFatalException("The library suite on the SPOT doesn't match the library suite used to build this application. Please do 'ant flashlibrary' before deploying this application.");
        }
        Flashable flashableVersionOfBinTempFile = new Flashable(binFilePath);

        helper.info("About to flash from " + appSuiteDir);
        try {
            helper.getAdminTarget().sendFile(flashableVersionOfBinTempFile);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw (SpotClientException) new Exception(ex.getMessage());
        }
        helper.getAdminTarget().checkResponse();
        binFilePath.delete();

        if (isMaster.equalsIgnoreCase("true")) {
            String oldStartupUri = configPage.getStartupUri();
            try {
                helper.getCommand(SetStartupCommand.NAME).execute(helper, uri, midletNumber);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw (SpotClientException) new Exception(ex.getMessage());
            }

            if (!oldStartupUri.equals(uri) && !oldStartupUri.equals(ConfigPage.LIBRARY_URI)) {
                helper.getCommand(UndeployCommand.NAME).execute(helper, oldStartupUri);
            }
        }
        return null;
    }

    private File getManifestFile(File f) {
        String[] paths = new String[]{
                "META-INF/manifest.mf",
                "META-INF/MANIFEST.MF",
                "META-INF/Manifest.mf",
                "meta-inf/manifest.mf",
                "meta-inf/MANIFEST.MF",
                "meta-inf/Manifest.mf",
                "Meta-inf/manifest.mf",
                "Meta-inf/MANIFEST.MF",
                "Meta-inf/Manifest.mf",
        };
        for (int i = 0; i < paths.length; i++) {
            File file = new File(f.getParentFile(), paths[i]);
            if (file.exists()) {
                return file;
            }
        }
        throw new SpotClientFailureException("Cannot find manifest file in folder " + f.getParentFile());
    }

    public int getSignature() {
        return SIGNATURE_OPTIONAL_FOURTH_STRING;
    }

    public String getName() {
        return NAME;
    }

    public String getUsage() {
        return "flashapp isMaster midletNumber uriOrAsterisk [name] -- flash the Java application";
    }

    private String getAppFilePath(String name) {
        File f = new File(name + ".suite");
        if (!f.exists()) {
            String secondTry = name + File.separator + "suite" + File.separator + "image";
            f = new File(secondTry + ".suite");
            if (f.exists()) {
                name = secondTry;
            }
        }
        return name;
    }
}
