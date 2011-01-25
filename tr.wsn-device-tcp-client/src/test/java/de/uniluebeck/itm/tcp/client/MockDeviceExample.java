package de.uniluebeck.itm.tcp.client;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

public class MockDeviceExample {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		//final OperationQueue queue = new PausableExecutorOperationQueue();
//		//final MockConnection connection = new MockConnection();
//		//final Device device = new MockDevice(connection);
//		final RemoteConnection connection = new RemoteConnection();
//		
//		//connection.connect("MockPort");
//		connection.connect("1:testUser:testPassword@localhost:8080");
//		System.out.println("Connected");
//
//		//final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
//		
//		final DeviceAsync deviceAsync = new RemoteDevice(connection);
//		
//		System.out.println("Message packet listener added");
//		deviceAsync.addListener(new MessagePacketListener() {
//			public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
//				System.out.println("Message: " + new String(event.getMessage().getContent()));
//			}
//		},PacketType.LOG);
//		
//		System.out.println("Reading mac address...");
//		
//		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
//			public void onProgressChange(float fraction) {
//				final int percent = (int) (fraction * 100.0);
//				System.out.println("Reading mac address progress: " + percent + "%");
//			}
//			
//			public void onSuccess(MacAddress result) {
//				System.out.println("Mac Address: " + result.getMacString());
//			}
//			
//			public void onFailure(Throwable throwable) {
//				throwable.printStackTrace();
//			}
//		};
//		
//		deviceAsync.readMac(1000000, callback);
//		
//		System.out.println("Setting Mac Address");
//		deviceAsync.writeMac(new MacAddress(1024), 10000, new AsyncAdapter<Void>() {
//
//			@Override
//			public void onProgressChange(float fraction) {
//				final int percent = (int) (fraction * 100.0);
//				System.out.println("Writing mac address progress: " + percent + "%");
//			}
//
//			@Override
//			public void onSuccess(Void result) {
//				System.out.println("Mac Address written");
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
//				throwable.printStackTrace();
//			}
//		});
//		
//		OperationHandle<MacAddress> han = deviceAsync.readMac(10000000, callback);
//		System.out.println(han.get());
//		
//		System.out.println();
//		
//		final OperationHandle<ChipType> handle = deviceAsync.getChipType(100000, new AsyncAdapter<ChipType>() {
//
//			@Override
//			public void onProgressChange(float fraction) {
//				final int percent = (int) (fraction * 100.0);
//				System.out.println("Reading chip type progress: " + percent + "%");
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
//				throwable.printStackTrace();
//			}
//			@Override
//			public void onSuccess(ChipType result) {
//				System.out.println("Chip Type onSuccess: " + result.getName());
//			}
//		});
//		
//		System.out.println("Chip Type: " + handle.get());
//		//queue.shutdown(true);
//		//System.out.println("Queue terminated");
//		//connection.shutdown(false);
//		//System.out.println("Connection closed");
//	}
	
	RemoteConnection connection;
	DeviceAsync deviceAsync;
	
	public MockDeviceExample() {
		connection = new RemoteConnection();
		connection.connect("1:testUser:testPassword@localhost:8080");
		System.out.println("Connected");
		deviceAsync = new RemoteDevice(connection);
	}
	
	public void addListener(){
		System.out.println("Message packet listener added");
		deviceAsync.addListener(new MessagePacketListener() {
			public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
				System.out.println("Message: " + new String(event.getMessage().getContent()));
			}
		}, PacketType.LOG);
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
		
		deviceAsync.readMac(100000, callback);
		
		System.out.println("Setting Mac Address");
		deviceAsync.writeMac(new MacAddress(1024), 100000, new AsyncAdapter<Void>() {

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
	
	public void exampleResetOperation(){
		deviceAsync.reset(10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reset Operation: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Reset Done");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
	
	public void exampleEraseFlashOperation(){
		deviceAsync.eraseFlash(10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("erase Process: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Flash erased");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
	
	public void exampleProgramOperation(){
		byte[] data = {0,0,0,0,0,0,1};
		deviceAsync.program(data, 100000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("program Process: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Program Done");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
	
	public void exampleFlashOperation(){
		
		final AsyncCallback<byte[]> callback = new AsyncAdapter<byte[]>() {
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading flash progress: " + percent + "%");
			}
			
			public void onSuccess(byte[] result) {
				System.out.println("Flash data: " + result.toString());
			}
			
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		};
		
		deviceAsync.readFlash(0, 10, 10000, callback);
		
		byte[] data = {0,0,0,0,1};
		deviceAsync.writeFlash(0, data, 100, 100000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing flash progress: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Flash written");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		deviceAsync.readFlash(0, 10, 10000, callback);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final MockDeviceExample example = new MockDeviceExample();
		example.addListener();
		//example.exampleMacAddressOperations();
		//example.exampleChipTypeOperation();
		//example.exampleSendOperation("00000000");
		example.exampleResetOperation();
		//example.exampleEraseFlashOperation();
		//example.exampleProgramOperation();
		//example.exampleFlashOperation();
	}
	
	
	
}
