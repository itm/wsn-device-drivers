package de.uniluebeck.itm.devicedrivier.exmaple;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacketAdapter;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

public class MockDeviceExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final OperationQueue queue = new SingleThreadOperationQueue();
		final Device device = new MockDevice();
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		System.out.println("Message packet listener added");
		deviceAsync.addMessagePacketListener(new MessagePacketAdapter() {
			public void onMessagePlainTextReceived(MessagePlainText message) {
				System.out.println("Message: " + message);
			}
		}, PacketType.LOG);
		
		System.out.println("Reading mac address...");
		
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent + "%");
			}
			
			public void onSuccess(MacAddress result) {
				System.out.println("Mac Address: " + result.getMacString());
			}
			
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		};
		
		deviceAsync.readMac(10000, callback);
		
		System.out.println("Setting Mac Address");
		deviceAsync.writeMac(new MacAddress(1024), 10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing mac address progress: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Mac Address written");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		deviceAsync.readMac(10000, callback);
		
		final OperationHandle<ChipType> handle = deviceAsync.getChipType(10000, new AsyncAdapter<ChipType>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading chip type progress: " + percent + "%");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		System.out.println("Chip Type: " + handle.get());
		System.out.println("Finsihed");
	}

}
