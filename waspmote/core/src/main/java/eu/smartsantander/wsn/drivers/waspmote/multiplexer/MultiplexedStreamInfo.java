package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author TLMAT UC
 */
public class MultiplexedStreamInfo {

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public MultiplexedStreamInfo(OutputStream outputStream, InputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
