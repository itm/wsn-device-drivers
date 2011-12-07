package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.JarUtil;
import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author TLMAT UC
 */
public class WaspmoteMultiplexedSerialPortConnection extends AbstractConnection {

    private static final Logger logger = LoggerFactory.getLogger(WaspmoteMultiplexedSerialPortConnection.class);

    public static final int DEFAULT_BAUDRATE = 38400;
    public static final int DEFAULT_MAX_RETRIES = 5;
    private static final int SLEEP_BETWEEN_RETRIES = 200;
    private static final int MAX_CONNECTION_TIMEOUT = 1000;

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int baudrate = DEFAULT_BAUDRATE;
    private int stopBits = SerialPort.STOPBITS_1;
    private int dataBits = SerialPort.DATABITS_8;
    private int parityBit = SerialPort.PARITY_NONE;

    private SerialPort serialPort;
    private WaspmoteConnectionMultiplexer multiplexer;

    static {
        JarUtil.loadLibrary("rxtxSerial");
        logger.debug("Loading rxtxSerial from jar file");
    }

    @Inject
    private WaspmoteMultiplexedSerialPortConnection() {
        super();
    }

    public void setMultiplexer(WaspmoteConnectionMultiplexer multiplexer) {
        this.multiplexer = multiplexer;
    }

    @Override
    public synchronized void connect(String port) {
        Preconditions.checkNotNull(this.multiplexer, "Waspmote serial port multiplexer has not been set.");
        if (!isConnected()) {
            Preconditions.checkNotNull(port, "The given port can not be null.");
            try {
                connectSerialPort(port);
                clear();
                setConnected();
                serialPort.addEventListener(multiplexer);
                serialPort.notifyOnDataAvailable(true);
            } catch (PortInUseException e) {
                logger.error("Port already in use. Connection will be removed. ");
                if (serialPort != null) {
                    serialPort.close();
                }
                throw new RuntimeException(e);
            } catch (Exception e) {
                if (serialPort != null) {
                    serialPort.close();
                }
                logger.error("Port " + port + " does not exist. Connection will be removed. " + e, e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Connect to the real multiplexed serial port.
     *
     * @param port The port string.
     * @throws Exception
     */
    private void connectSerialPort(String port) throws Exception {
        SerialPort sp = null;
        CommPortIdentifier cpi = (CommPortIdentifier) CommPortIdentifier.getPortIdentifier(port);
        CommPort commPort = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                commPort = cpi.open(this.getClass().getName(), MAX_CONNECTION_TIMEOUT);
                break;
            } catch (PortInUseException piue) {
                logger.error("Port in Use Retrying to connect");
                if (i >= maxRetries - 1) {
                    throw piue;
                }
                Thread.sleep(SLEEP_BETWEEN_RETRIES);
            }
        }
        if (commPort instanceof SerialPort) {
            sp = (SerialPort) commPort;
        } else {
            logger.debug("Port is no SerialPort");
        }

        serialPort = sp;
        logger.debug("Set com port " + baudrate + " " + dataBits + " " + stopBits + " " + parityBit);
        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parityBit);
            serialPort.setDTR(false);
            serialPort.setRTS(false);
        } catch (UnsupportedCommOperationException e) {
            logger.warn("Problem while setting serial port params", e);
        }

        setUri(port);
        setOutputStream(serialPort.getOutputStream());
        setInputStream(serialPort.getInputStream());
    }


    @Override
    public void close() throws IOException {
        this.shutdown(false);
    }

    public void shutdown(boolean force) {
        if (force) {
            // Not sure of the implications of closing serial ports when devices are still connected
            this.shutdownPort();
        } else {
            if (!multiplexer.isAnyNodeRegistered()) {
                this.shutdownPort();
            }
        }
    }

    private void shutdownPort() {
        try {
            if (getInputStream() != null) {
                getInputStream().close();
            }
        } catch (final IOException e) {
            logger.error("Failed to close in-stream :" + e, e);
        }
        try {
            if (getOutputStream() != null) {
                getOutputStream().close();
            }
        } catch (final IOException e) {
            logger.error("Failed to close out-stream :" + e, e);
        }
        if (serialPort != null) {
            serialPort.close();
            serialPort.removeEventListener();
            serialPort = null;
        }
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int normalBaudrate) {
        this.baudrate = normalBaudrate;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopbits) {
        this.stopBits = stopbits;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDatabits(int databits) {
        this.dataBits = databits;
    }

    public int getParityBit() {
        return parityBit;
    }

    public void setParityBit(int normalParityBit) {
        this.parityBit = normalParityBit;
    }

    @Override
    public int[] getChannels() {
        return null;  //Do not know what it means
    }

}
