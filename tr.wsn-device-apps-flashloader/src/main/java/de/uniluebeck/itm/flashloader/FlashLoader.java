package de.uniluebeck.itm.flashloader;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
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
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.jennic.JennicDevice;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;
import de.uniluebeck.itm.devicedriver.pacemate.PacemateDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.telosb.TelosbDevice;
import de.uniluebeck.itm.tcp.client.RemoteConnection;
import de.uniluebeck.itm.tcp.client.RemoteDevice;

/**
 * Class FlashLoader.
 */
public class FlashLoader {
	
	String port;
	String server;
	String user;
	String password;
	String device_parameter;
	DeviceAsync deviceAsync;
	
	boolean flashed = false;	//for the test-class
	String current_mac_adress;		//for the test-class
	boolean geresetet = false;		//for the test-class
	
	/**
	 * Instantiates a new flash loader.
	 */
	public FlashLoader(){

	}
	
	/**
	 * Sets the device.
	 *
	 * @param device the new device
	 */
	public void setDevice(String device){
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
	
	/**
	 * Connect.
	 */
	public void connect(){
		if(server != null){
			final RemoteConnection connection = new RemoteConnection();
			
			connection.connect("1:"+user+":"+password+"@localhost:8080");
			System.out.println("Connected");
			
			deviceAsync = new RemoteDevice(connection);
		}
		else{
			final OperationQueue queue = new PausableExecutorOperationQueue();
			final MockConnection connection = new MockConnection();
			Device device = new MockDevice(connection);
			
			if(device_parameter != null){
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
	 * Flash.
	 *
	 * @param file the file
	 */
	public void flash(String file){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("File: " + file);
		
		System.out.println("Program the Device");
		deviceAsync.program(file.getBytes(), 100000, new AsyncAdapter<Void>() {
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("The Device has been flashed.");
				flashed = true;         //fuer den Test
				System.exit(0);
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
				System.exit(0);
			}
		});
	}
	
	/**
	 * Readmac.
	 */
	public void readmac(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
		System.out.println("Reading mac address...");
		
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent + "%");
			}
			
			public void onSuccess(MacAddress result) {
				System.out.println("Mac Address: " + result.getMacString());
				current_mac_adress = result.getMacString();
				System.exit(0);
			}
			
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
				System.exit(0);
			}
		};
		
		deviceAsync.readMac(10000, callback);
	}
	
	/**
	 * Writemac.
	 *
	 * @param macAdresse the mac adresse
	 */
	public void writemac(MacAddress macAdresse){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
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
				System.exit(0);
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
				System.exit(0);
			}
		});
	}
	
	/**
	 * Reset.
	 */
	public void reset(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
		System.out.println("Reset");
		deviceAsync.reset(10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reset the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Device has been reseted");
				geresetet = true;		//fuer den Test
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