package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Inject;
import de.uniluebeck.itm.netty.handlerstack.dlestxetx.DleStxEtxFramingEncoder;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import static com.google.common.base.Preconditions.checkState;

public class SunspotDevice implements Device, SunspotBaseStationListener {

    private static final Logger log = LoggerFactory.getLogger(SunspotDevice.class);

    private String macAddress;

    private SunspotBaseStation baseStation;

    private volatile boolean connected;

    private OutputStream outgoingOutputStream;

    private InputStream incomingInputStream;

    private InputStream outgoingInputStream;

    private OutputStream incomingOutputStream;

    private HashMap<String, String> deviceConfiguration;

    @Inject
    public SunspotDevice(SunspotBaseStation baseStation, String nodeID) {
        this.macAddress = nodeID;
        this.baseStation = baseStation;
    }

    @Override
    public OperationFuture<Void> program(byte[] data, long timeout, OperationCallback<Void> callback) {
        checkState(connected, "Device not connected.");
        baseStation.start();
        try {
            baseStation.program(this.macAddress,data,timeout,callback);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<Void> reset(long timeout, OperationCallback<Void> callback) {
        checkState(connected, "Device not connected.");
        baseStation.start();
        return baseStation.resetNode(this.macAddress, timeout, callback);
    }

    @Override
    public void addListener(ConnectionListener listener) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public void removeListener(ConnectionListener listener) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public InputStream getInputStream() {
        return outgoingInputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outgoingOutputStream;
    }


    @Override
    public OperationFuture<ChipType> getChipType(long timeout, OperationCallback<ChipType> callback) {
        baseStation.start();
        return baseStation.getChipType(this.macAddress, timeout, callback);
    }


    @Override
    public OperationFuture<Void> eraseFlash(long timeout, OperationCallback<Void> callback) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public OperationFuture<Void> writeFlash(int address, byte[] data, int length, long timeout, OperationCallback<Void> callback) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public OperationFuture<byte[]> readFlash(int address, int length, long timeout, OperationCallback<byte[]> callback) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public OperationFuture<MacAddress> readMac(long timeout, OperationCallback<MacAddress> callback) {
        throw (new UnsupportedOperationException());
    }

    @Override
    public OperationFuture<Void> writeMac(MacAddress macAddress, long timeout, OperationCallback<Void> callback) {
        throw (new UnsupportedOperationException());
    }


    @Override
    public OperationFuture<Void> send(byte[] message, long timeout, OperationCallback<Void> callback) {
        checkState(connected, "Device not connected.");
        throw (new UnsupportedOperationException());
    }

    @Override
    public void connect(String uri) throws IOException {
        baseStation.addListener(this);
        connected = true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }


    @Override
    public int[] getChannels() {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int waitDataAvailable(int timeout) throws TimeoutException, IOException {
        throw (new UnsupportedOperationException());
    }

    @Override
    public void clear() throws IOException {
        throw (new UnsupportedOperationException());
    }

    @Override
    public void close() throws IOException {
        baseStation.removeListener(this);
        connected = false;
    }


    public String getMacAddress() {
        return this.macAddress;
    }

    @Override
    public void messageReceived(byte[] messsageBytes) {
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(new DleStxEtxFramingEncoder());
        ChannelBuffer unencodedBuffer = ChannelBuffers.wrappedBuffer(messsageBytes);
        encoder.offer(unencodedBuffer);
        ChannelBuffer encodedBuffer = encoder.poll();
        try {
            encodedBuffer.writeBytes(outgoingInputStream, encodedBuffer.readableBytes());
        } catch (IOException e) {
            log.warn("IOException while writing to outgoingInputStream: {}", e);
        }
    }

    public void setConfiguration(HashMap<String, String> deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
        this.macAddress = this.deviceConfiguration.get("macAddress");
    }
}
