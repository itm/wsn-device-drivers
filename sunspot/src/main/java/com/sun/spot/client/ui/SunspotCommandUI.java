package com.sun.spot.client.ui;

import com.sun.spot.client.*;
import com.sun.spot.client.command.SynchronizeCommand;
import com.sun.spot.peripheral.ota.ISpotAdminConstants;
import com.sun.spot.peripheral.radio.LowPan;
import com.sun.spot.peripheral.radio.mhrp.aodv.AODVManager;
import com.sun.spot.peripheral.radio.mhrp.lqrp.LQRPManager;
import com.sun.spot.spotselector.SpotInfo;
import com.sun.spot.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/1/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SunspotCommandUI extends SpotClientCommandLineUI {
    private static final String PROMPT = "BL>";
    private BufferedReader commandLineReader;
    private String remoteAddr;
    private static boolean verbose = false;
    private SpotClientCommands commandRepository;
    private int totalSteps;
    private int lastDisplayStepsComplete;
    private static final String REMOTE_ADDRESS_FLAG = "-remote.address=";
    private static final String REMOTE_CHANNEL_FLAG = "-remote.channel=";
    private static final String REMOTE_PAN_ID_FLAG = "-remote.pan.id=";
    private static final String REMOTE_TRANSMIT_POWER_FLAG = "-remote.transmit.power=";
    private static final int NUMBER_OF_DISPLAY_STEPS = 20;
    private static final int COLUMN_WIDTH = 60;
    private static final Logger log = LoggerFactory.getLogger(SunspotCommandUI.class);
    private String scriptString;

    public SunspotCommandUI() {

    }

    public void initialize(String[] args) throws Exception {
        File sysBinPath = new File(".");
        String appPath = ".";
        String keyStorePath = null;
        String libFile = null;
        String port = null;
        String portAndIdList = null;
        String idOnPort = null;
        String remoteAddr = null;
        File scriptFile = null;
        boolean deleteScriptFile = false;
        boolean synchronisationRequired = true;
        boolean useLQRM = false;
        boolean useAODV = false;
        int i = 0;

        while (i < args.length) {
            if (args[i].startsWith("-")) {
                Argument arg = new Argument(args[i]);
                if (arg.matches("-sysBin")) {
                    sysBinPath = arg.fileValue();
                } else if (arg.matches("-keyStorePath")) {
                    keyStorePath = arg.stringValue() + File.separator;
                } else if (arg.matches("-libFile")) {
                    libFile = arg.stringValue();
                } else if (arg.matches("-v")) {
                    verbose = true;
                } else if (arg.matches("-p")) {
                    /*
                     * We don't change the case of the port argument here,
                     * but in SerialPort.java the search is done using case
                     * insensitive matching.
                     */
                    port = arg.stringValueWithoutQuotes();
                } else if (arg.matches("-i")) {
                    portAndIdList = arg.stringValueWithoutQuotes().trim();
                } else if (arg.matches(REMOTE_CHANNEL_FLAG)) {
                    if (arg.stringValue().length() > 0) {
                        System.setProperty("remote.channel", arg.stringValue());
                    }
                } else if (arg.matches(REMOTE_PAN_ID_FLAG)) {
                    if (arg.stringValue().length() > 0) {
                        System.setProperty("remote.pan.id", arg.stringValue());
                    }
                } else if (arg.matches(REMOTE_TRANSMIT_POWER_FLAG)) {
                    if (arg.stringValue().length() > 0) {
                        System.setProperty("remote.transmit.power", arg.stringValue());
                    }
                } else if (arg.matches(REMOTE_ADDRESS_FLAG)) {
                    remoteAddr = arg.stringValue();
                } else if (arg.matches("-app")) {
                    appPath = arg.stringValue();
                } else if (arg.matches("-f")) {
                    scriptFile = arg.fileValue();
                } else if (arg.matches("-F")) {
                    scriptFile = arg.fileValue();
                    deleteScriptFile = true;
                } else if (arg.matches("-nosync")) {
                    synchronisationRequired = false;
                } else if (arg.matches("-lqrp")) {
                    useLQRM = true;
                } else if (arg.matches("-aodv")) {
                    useAODV = true;
                } else if (arg.matches("-scriptString=")) {
                    this.scriptString = arg.stringValueWithoutQuotes();
                    verbose = true;
                } else {
                    System.err.println("Unrecognised argument: " + args[i]);
                }
            } else {
                break;
            }
            i++;
        }

        if (port == null) {
            log.error("Must specify a port with -p");
        } else if (portAndIdList != null) {
            SpotInfo[] siArray = SpotInfo.getSpotInfos(portAndIdList);
            for (int j = 0; j < siArray.length; j++) {
                if (port.equalsIgnoreCase(siArray[j].getPort())) {
                    idOnPort = siArray[j].getId();
                    break;
                }
            }
        }

        if (libFile == null) {
            libFile = sysBinPath + File.separator + "spotlib";
        }

        if (keyStorePath == null) {
            log.error("Must specify -keyStorePath");
        }

        SunspotCommandUI ui = new SunspotCommandUI();
        ui.info("SPOT Client starting...");
        try {
            SpotClientCommands commandRepository = null;

            if ((useLQRM || useAODV) && remoteAddr != null) {
                if (port != null) {
                    System.setProperty("SERIAL_PORT", port);
                }
                if (useLQRM) {
                    LowPan.getInstance().setRoutingManager(LQRPManager.getInstance());
                } else {
                    LowPan.getInstance().setRoutingManager(AODVManager.getInstance());
                }
            }

            if (remoteAddr == null && synchronisationRequired) {
                commandRepository = new SpotClientCommands(ui, appPath, libFile, sysBinPath, keyStorePath, port);

            } else {
                commandRepository = new SpotClientCommands(ui, appPath, libFile, sysBinPath, keyStorePath, port,
                        remoteAddr, ISpotAdminConstants.MASTER_ISOLATE_ECHO_PORT);
            }

            ui.diagnostic("about to init commands...");
            ui.initCommands(commandRepository);

            ui.setRemoteAddr(remoteAddr);
            // Not using takeControlOf(dc) from IUI because we want to take advantage
            // of the commandline UI's special ability to cope with script files.
            if (synchronisationRequired) {
                ui.synchronise(port, idOnPort);
            }
            ui.diagnostic("about to pass control to ui...");

            //ui.takeControlOf(scriptFile, deleteScriptFile);
            ui.takeControlOf2(this.scriptString);
        } catch (EOFException e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        } catch (SpotSerialPortNotFoundException e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }

    private void initCommands(SpotClientCommands commandRepository) {
        this.commandRepository = commandRepository;
        commandRepository.addCommand(new HelpCommand(this));
    }

    private void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     * Synchronise with the target - waits for the target to startup and then signals
     * that we want to interact with the bootloader.
     */
    private void synchronise(String port, String idOnPort) throws Exception {
        SpotNeedsResettingNotifier spotNeedsResettingNotifier = null;
        if (remoteAddr == null) {
            spotNeedsResettingNotifier = new SpotNeedsResettingNotifier(port, idOnPort);
            spotNeedsResettingNotifier.start();
        } else {
            System.out.println("Waiting for target to synchronise... ");
            System.out.println("(please wait for remote SPOT " + remoteAddr + " to respond)");
            System.out.println("(if no response ensure SPOT has OTA enabled)");
        }
        String blIntro = "";

        try {
            blIntro = (String) commandRepository.execute(SynchronizeCommand.NAME);
        } catch (SpotObsoleteVersionException e) {
            log.info("WARNING: " + e.getMessage());
            blIntro = e.getBootloaderIdentificationString();
        } catch (SpotClientException e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
        if (spotNeedsResettingNotifier != null) {
            spotNeedsResettingNotifier.setDone(true);
        }

        log.info(blIntro);
    }

    private static class SpotNeedsResettingNotifier extends Thread {

        private boolean done = false;
        private String port;
        private String idOnPort;

        public SpotNeedsResettingNotifier(String port, String idOnPort) {
            this.port = port;
            this.idOnPort = idOnPort;
        }

        public void run() {
            Utils.sleep(3000);
            if (!done) {
                log.info("(please reset SPOT " +
                        ((idOnPort == null || idOnPort.equals("")) ? "" : idOnPort + " ") + "on port " + port + ")");
            }
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }

    /**
     * Instruct the command line UI to take control.
     *
     * @param script           file containing commands to execute separate by line feeds, or null if no script
     * @param deleteScriptFile if true and script file not null, delete script file after use
     * @throws java.io.IOException
     */
    public void takeControlOf(File script, boolean deleteScriptFile) throws Exception {
        boolean executingScript = false;
        if (script != null) {
            InputStream in = new FileInputStream(script);
            int scriptSize = in.available();
            byte[] buffer = new byte[scriptSize];
            int numRead = in.read(buffer);
            in.close();
            if (deleteScriptFile) {
                script.delete();
            }
            if (numRead != scriptSize) {
                throw new IOException("Didn't read number of bytes that were available");
            }
            initCommandReader(new ByteArrayInputStream(buffer));
            executingScript = true;
        } else {
            initCommandReader(System.in);
        }
        if (!executingScript) {
            log.info("Sun SPOT Client");
        }
        while (true) {
            try {
                String cmd = getUserInput(executingScript && !verbose);
                if (cmd == null && executingScript) {
                    executingScript = false;
                    initCommandReader(System.in);
                    cmd = readUserInput();
                    log.info(cmd);
                }
                if (executingScript && verbose) {
                    // echo

                }
                log.info("COMMAND:" + cmd);
                if (cmd == null) break;
                processUserCommand(cmd);
            } catch (SpotClientException e) {
                log.error(e.getMessage());
                throw new Exception(e.getMessage());
            }
        }
    }


    public void takeControlOf2(String script) throws Exception {
        String[] commands = script.split(":");

        for (int i = 0; i < commands.length; i++) {
            try {
                String cmd = commands[i];
                log.info("COMMAND:" + cmd);
                processUserCommand(cmd);
            } catch (SpotClientException e) {
                log.error(e.getMessage());
                throw new Exception(e.getMessage());
            }
        }
    }


    private void initCommandReader(InputStream in) {
        commandLineReader = new BufferedReader(new InputStreamReader(in));
    }

    private String getUserInput(boolean hidePrompt) throws IOException {
        if (!hidePrompt) {
            System.out.println(PROMPT);
        }
        return readUserInput();
    }

    private String readUserInput() throws IOException {
        String cmd = commandLineReader.readLine();
        return cmd;
    }

    private void processUserCommand(String command) throws Exception, IOException {
        int index = command.indexOf(' ');
        String commandName = "";
        String commandArg = "";
        if (index == -1) {
            commandName = command;
        } else {
            commandName = command.substring(0, index);
            commandArg = command.substring(index + 1).trim();
        }
        if (commandRepository.containsCommand(commandName)) {
            String[] args = parseArgs(commandArg);
            Object result = null;
            try {
                result = commandRepository.execute(commandName, args);
            } catch (SpotClientArgumentException e) {
                log.error(e.getMessage());
                throw new Exception(e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new Exception(e.getMessage());
            }
            if (result != null) {
                log.debug((String) result);
                throw new Exception((String) result);
            }
        } else {
            log.error("Unrecognised command: " + commandName);
        }
    }

    public void quit() {
        log.info("Quiting SunspotCommandUI");
    }

}
