package de.uniluebeck.itm.messenger;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.jennic.JennicDevice;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;
import de.uniluebeck.itm.devicedriver.pacemate.PacemateDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.telosb.TelosbDevice;
import de.uniluebeck.itm.tcp.client.RemoteConnection;
import de.uniluebeck.itm.tcp.client.RemoteDevice;

public class Messenger {
	
	String port;
	String server;
	String user;
	String passwort;
	String device_parameter;
	boolean gesendet = false; 		//fuer den Test
	DeviceAsync deviceAsync;
	
	public Messenger(){

	}
	
	public void setDevice(String device) {
		this.device_parameter = device;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void connect(){
		if(server != null){
			final RemoteConnection connection = new RemoteConnection();
			
			connection.connect("1:"+user+":"+passwort+"@localhost:8080");
			System.out.println("Connected");
			
			deviceAsync = new RemoteDevice(connection);
		}
		else{
			final OperationQueue queue = new PausableExecutorOperationQueue();
			final MockConnection connection = new MockConnection();
			Device device = new MockDevice(connection);
			
			if(device_parameter.equals("isense")){
				//TODO
			}
			else if(device_parameter.equals("jennec")){
				SerialPortConnection jennic_connection = new iSenseSerialPortConnection();
				jennic_connection.addListener(new ConnectionListener() {
					@Override
					public void onConnectionChange(ConnectionEvent event) {
						if (event.isConnected()) {
							System.out.println("Connection established with port " + event.getUri());
						}				
					}
				});
				device = new JennicDevice(jennic_connection);	
				jennic_connection.connect("COM19");	
			}
			else if(device_parameter.equals("pacemate")){
				SerialPortConnection pacemate_connection = new iSenseSerialPortConnection();
				pacemate_connection.addListener(new ConnectionListener() {
					@Override
					public void onConnectionChange(ConnectionEvent event) {
						if (event.isConnected()) {
							System.out.println("Connection established with port " + event.getUri());
						}				
					}
				});
				device = new PacemateDevice(pacemate_connection);	
				pacemate_connection.connect("COM19");
			}
			else if(device_parameter.equals("telosb")){
				SerialPortConnection telosb_connection = new iSenseSerialPortConnection();
				telosb_connection.addListener(new ConnectionListener() {
					@Override
					public void onConnectionChange(ConnectionEvent event) {
						if (event.isConnected()) {
							System.out.println("Connection established with port " + event.getUri());
						}				
					}
				});
				device = new TelosbDevice(telosb_connection);	
				telosb_connection.connect("COM19");
			}
			deviceAsync = new QueuedDeviceAsync(queue, device);
			
			System.out.println("Message packet listener added");
			deviceAsync.addListener(new MessagePacketListener() {
				public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
					System.out.println("Message: " + new String(event.getMessage().getContent()));
				}
			}, PacketType.LOG);
		}
	}
	
	public void send(String message){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Message: " + message);
		
		System.out.println("Message packet listener added");
		deviceAsync.addListener(new MessagePacketListener() {
			public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
				System.out.println("Message: " + new String(event.getMessage().getContent()));
			}
		},PacketType.LOG);
		
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
				gesendet = true;		//fuer den Test
				System.exit(0);
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
				System.exit(0);
			}
		});
	}
}