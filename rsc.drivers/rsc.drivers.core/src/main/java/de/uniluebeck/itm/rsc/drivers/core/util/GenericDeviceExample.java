package de.uniluebeck.itm.rsc.drivers.core.util;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.Connection;
import de.uniluebeck.itm.rsc.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.rsc.drivers.core.ConnectionListener;
import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacketListener;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.rsc.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.rsc.drivers.core.event.MessageEvent;
import de.uniluebeck.itm.rsc.drivers.core.nulldevice.NullConnection;
import de.uniluebeck.itm.rsc.drivers.core.nulldevice.NullDevice;

public class GenericDeviceExample implements MessagePacketListener, ConnectionListener {

	private final OperationQueue queue = new PausableExecutorOperationQueue();
	
	private Device<?> device = new NullDevice();
	
	private Connection connection = new NullConnection();
	
	private DeviceAsync deviceAsync;
	
	private File image;
	
	private String uri;
	
	private MessagePacket messagePacket;

	public void setDevice(Device<?> device) {
		this.device = device;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void setMessagePacket(MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}

	private void init() {
		device.addListener(this);
		connection = device.getConnection();
		connection.addListener(this);
		deviceAsync = new QueuedDeviceAsync(queue, device);
	}
	
	public void connect() {
		System.out.println("Connecting to: " + uri);
		connection.connect(uri);
	}
	
	public void programImage() throws IOException {
		if (image == null) {
			System.out.println("Program skipped cause no image set.");
			return;
		}
		
		final AsyncCallback<Void> callback = new AsyncAdapter<Void>() {
			@Override
			public void onExecute() {
				System.out.println("Flashing image...");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(Void result) {
				System.out.println("Image successfully flashed");
			}
		};
		
		final byte[] bytes = Files.toByteArray(image);
		System.out.println("Image length: " + bytes.length);
	    deviceAsync.program(bytes, 600000, callback);
	}
	
	public void resetOperation() {
		final AsyncCallback<Void> callback = new AsyncAdapter<Void>() {
			public void onExecute() {
				System.out.println("Resetting device...");
			}
			
			@Override
			public void onSuccess(Void result) {
				System.out.println("Device successful reseted");
			}
		};
		deviceAsync.reset(10000, callback);
	}
	
	public void macAddressOperations() {		
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			
			@Override
			public void onExecute() {
				System.out.println("Reading mac address...");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(MacAddress result) {
				System.out.println("Mac Address: " + result);
			}
		};
		
		deviceAsync.readMac(100000, callback);
		
		
		deviceAsync.writeMac(new MacAddress(1024), 300000, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Setting Mac Address");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing mac address progress: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Mac Address written");
			}
		});
		deviceAsync.readMac(10000, callback);
	}
	
	public void readFlashOperation() {
		final AsyncCallback<byte[]> callback = new AsyncAdapter<byte[]>() {
			
			@Override
			public void onExecute() {
				System.out.println("Read flash from 0 to 32...");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading flash progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(byte[] result) {
				System.out.println("Reading result: " + result);
			}
		};
		deviceAsync.readFlash(0, 32, 10000, callback);
	}
	
	public void chipTypeOperation() {
		deviceAsync.getChipType(100000, new AsyncAdapter<ChipType>() {

			@Override
			public void onExecute() {
				System.out.println("Reading ChipType from device...");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading chip type progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(ChipType result) {
				System.out.println("Chip Type: " + result);
			}
		});
	}
	
	public void sendOperation() {
		deviceAsync.send(messagePacket, 10000, new AsyncAdapter<Void>() {
			public void onExecute() {
				System.out.println("Sending message");
			}
			
			public void onSuccess(Void result) {
				System.out.println("Message send");
			}
		});
	}
	
	public void waitForOperationsToFinish() {
		// Wait until the queue is empty.
		while (!queue.getOperations().isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		System.out.println("Shutting down queue...");
		queue.shutdown(false);
		System.out.println("Queue terminated");
		System.out.println("Closing connection...");
		connection.shutdown(true);
		System.out.println("Connection closed");
	}
	
	public void waitForMessagePackets() {
		System.out.println("Waiting for messages from the device.");
		System.out.println("Press any key to shutdown...");
		try {
			while(System.in.read() == -1) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		init();
		connect();
		try {
			programImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		macAddressOperations();
		readFlashOperation();
		chipTypeOperation();
		sendOperation();
		resetOperation();
		waitForOperationsToFinish();
		waitForMessagePackets();
		shutdown();
	}
	
	@Override
	public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
		System.out.println(new String(event.getMessage().getContent()).substring(1));
	}
	
	@Override
	public void onConnectionChange(ConnectionEvent event) {
		System.out.println("Connected with port: " + event.getUri());
	}
}
