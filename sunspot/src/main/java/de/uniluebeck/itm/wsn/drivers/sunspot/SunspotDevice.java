package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SunspotDevice implements Device {

    @Inject
    private SunspotBaseStation baseStation;

    @Override
    public OperationFuture<Void> program(byte[] data, long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<Void> reset(long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addListener(ConnectionListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeListener(ConnectionListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getInputStream() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OutputStream getOutputStream() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public OperationFuture<ChipType> getChipType(long timeout, OperationCallback<ChipType> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public OperationFuture<Void> eraseFlash(long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<Void> writeFlash(int address, byte[] data, int length, long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<byte[]> readFlash(int address, int length, long timeout, OperationCallback<byte[]> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<MacAddress> readMac(long timeout, OperationCallback<MacAddress> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OperationFuture<Void> writeMac(MacAddress macAddress, long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public OperationFuture<Void> send(byte[] message, long timeout, OperationCallback<Void> callback) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connect(String uri) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isConnected() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isClosed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public int[] getChannels() {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int waitDataAvailable(int timeout) throws TimeoutException, IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() throws IOException {
        throw(new UnsupportedOperationException());
    }

    @Override
    public void close() throws IOException {
        throw(new UnsupportedOperationException());
    }


}
