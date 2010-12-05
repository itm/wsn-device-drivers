package de.uniluebeck.itm.messenger;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketAdapter;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

public class Messenger {
	
	String port;
	String server;
	
	public Messenger(){

	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void send(String message){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Message: " + message);
		
		final OperationQueue queue = new SingleThreadOperationQueue();
		final Device device = new MockDevice();
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		System.out.println("Message packet listener added");
		deviceAsync.addMessagePacketListener(new MessagePacketAdapter() {
			public void onMessagePlainTextReceived(MessagePlainText message) {
				System.out.println("Message: " + message);
			}
		}, PacketType.LOG);
		
		System.out.println("Send Message");
		MessagePacket packet = new MessagePacket(0, message.getBytes());
		deviceAsync.send(packet, 10000, new AsyncAdapter<Void>() {

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
		
		System.exit(0);
	}
}