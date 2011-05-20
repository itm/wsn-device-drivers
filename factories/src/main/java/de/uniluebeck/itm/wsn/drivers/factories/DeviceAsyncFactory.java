package de.uniluebeck.itm.wsn.drivers.factories;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.thread.PausableExecutorOperationQueue;

@SuppressWarnings("unused")
public abstract class DeviceAsyncFactory {

	public static DeviceAsync create(final DeviceType deviceType, final Connection connection) {
		return create(deviceType, connection, new PausableExecutorOperationQueue());
	}

	public static DeviceAsync create(final DeviceType deviceType, final Connection connection,
									 final OperationQueue operationQueue) {

		Device<? extends Connection> device = DeviceFactory.create(deviceType, connection);
		return new QueuedDeviceAsync(operationQueue, device);
	}

	public static DeviceAsync create(final String deviceType, final Connection connection) {
		return create(DeviceType.fromString(deviceType), connection);
	}

	public static DeviceAsync create(final String deviceType, final Connection connection,
									 final OperationQueue operationQueue) {
		return create(DeviceType.fromString(deviceType), connection, operationQueue);
	}

}
