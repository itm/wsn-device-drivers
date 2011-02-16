package de.uniluebeck.itm.devicedrivier.exmaple;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

public class MockDeviceExample {

	private final OperationQueue queue = new PausableExecutorOperationQueue();
	
	private final MockConnection connection = new MockConnection();
	
	private final Device<MockConnection> device = new MockDevice(connection);
	
	private final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
	
	public MockDeviceExample() {
		System.out.println("Message packet listener added");
		deviceAsync.addListener(new MessagePacketListener() {
			public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
				final byte[] content = event.getMessage().getContent();
				final byte[] message = new byte[content.length - 1];
				System.arraycopy(content, 1, message, 0, message.length);
				System.out.println("Message: " + new String(message));
			}
		}, PacketType.LOG);
	}
	
	public void connect() {
		connection.connect("MockPort");
		System.out.println("Connected");
	}
	
	public void exampleMacAddressOperations() {
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
	}
	
	public void exampleChipTypeOperation() {
		deviceAsync.getChipType(10000, new AsyncAdapter<ChipType>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading chip type progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(ChipType result) {
				System.out.println("Chip Type: " + result);
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
	
	public void exampleSendOperation(String message){
        MessagePacket packet = new MessagePacket(0, message.getBytes());
        deviceAsync.send(packet, 100000, new AsyncAdapter<Void>() {

            @Override
            public void onProgressChange(float fraction) {
                final int percent = (int) (fraction * 100.0);
                System.out.println("Sending the message: " + percent + "%");
            }

            @Override
            public void onSuccess(Void result) {
                System.out.println("Message send");
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    } 
	
	public void finish() {
		// Wait until the queue is empty.
		while (!queue.getOperations().isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Shutting down queue...");
		queue.shutdown(false);
		System.out.println("Queue terminated");
		System.out.println("Closing connection...");
		connection.shutdown(true);
		System.out.println("Connection closed");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final MockDeviceExample example = new MockDeviceExample();
		example.connect();
		example.exampleMacAddressOperations();
		example.exampleChipTypeOperation();
		example.exampleSendOperation("Hallo Welt");
		example.finish();
	}

}
