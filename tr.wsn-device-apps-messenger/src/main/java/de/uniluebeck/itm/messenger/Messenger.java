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
import de.uniluebeck.itm.devicedriver.telosb.TelosbSerialPortConnection;
import de.uniluebeck.itm.tcp.client.RemoteConnection;
import de.uniluebeck.itm.tcp.client.RemoteDevice;

/**
 * The Class Messenger.
 */
public class Messenger {
	
	String port;
	String server;
	String user;
	String password;
	String device_parameter;
	DeviceAsync deviceAsync;
	private String id;

	boolean gesendet = false; 		//for the test-class
	
	/**
	 * Instantiates a new messenger.
	 */
	public Messenger(){

	}
	
	/**
	 * Sets the device.
	 *
	 * @param device the new device
	 */
	public void setDevice(String device) {
		this.device_parameter = device;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Sets the server.
	 *
	 * @param server the new server
	 */
	public void setServer(String server) {
		this.server = server;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Connect.
	 */
	public void connect(){
		if(server != null){
			final RemoteConnection connection = new RemoteConnection();
			
			connection.connect(id+":"+user+":"+password+"@"+server+":"+port);
			System.out.println("Connected");
			
			deviceAsync = new RemoteDevice(connection);
		}
		else{
			final OperationQueue queue = new PausableExecutorOperationQueue();
			final MockConnection connection = new MockConnection();
			Device device = new MockDevice(connection);
			
			if(device_parameter != null){
				if(device_parameter.equals("jennec")){
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
					jennic_connection.connect(port);	
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
					pacemate_connection.connect(port);
				}
				else if(device_parameter.equals("telosb")){
					SerialPortConnection telosb_connection = new TelosbSerialPortConnection();
					telosb_connection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out.println("Connection established with port " + event.getUri());
							}				
						}
					});
					device = new TelosbDevice(telosb_connection);	
					telosb_connection.connect(port);
				}
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
	
	/**
	 * Send.
	 *
	 * @param message the message
	 */
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
				System.out.println("Message sent");
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