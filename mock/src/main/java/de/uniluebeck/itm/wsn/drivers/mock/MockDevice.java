package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class MockDevice extends SerialPortDevice {

	private final MockConnection mockConnection;

	public MockDevice(@Named("driverInputStream") final InputStream driverInputStream,
					  @Named("driverOutputStream") final OutputStream driverOutputStream,
					  @Named("pipeOutputStreamToDriverInputStream")
					  final OutputStream pipeOutputStreamToDriverInputStream,
					  @Named("pipeInputStreamFromDriverOutputStream")
					  final InputStream pipeInputStreamFromDriverOutputStream, final Connection deviceConnection,
					  final OperationFactory operationFactory,
					  final MockConnection mockConnection) {
		super(driverInputStream, driverOutputStream, pipeOutputStreamToDriverInputStream,
				pipeInputStreamFromDriverOutputStream, deviceConnection, operationFactory
		);
		this.mockConnection = mockConnection;
	}

	@Override
	public void releaseLockOnDeviceStreams() {
		super.releaseLockOnDeviceStreams();
		mockConnection.startAliveRunnable();
	}

	@Override
	public void acquireLockOnDevice() throws InterruptedException {
		mockConnection.stopAliveRunnable();
		super.acquireLockOnDevice();
	}
}
