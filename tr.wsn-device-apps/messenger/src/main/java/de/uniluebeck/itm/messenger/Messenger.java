package de.uniluebeck.itm.messenger;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
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
 * Sends a given Message to the sensor-device.
 */
public class Messenger {

	private String port;
	private String server;
	private String user;
	private String password;
	private String deviceParameter;
	private DeviceAsync deviceAsync;
	private String id;
	private byte messageType;
	private RemoteConnection connection;
	private boolean sent = false; // for the test-class

	/**
	 * Instantiates a new messenger.
	 */
	public Messenger(String port, String server, String user, String password, String device, String id, int messageType) {
		this.port = port;
		this.server = server;
		this.user = user;
		this.password = password;
		this.deviceParameter = device;
		this.id = id;
		this.messageType = (byte)messageType;
	}
	
	public RemoteConnection getConnection(){
		return connection;
	}
	
	/**
	 * Connect. Method to connect to the tcp-server or to a local sensornode.
	 */
	public void connect() {
		if (server != null) {
			// Connect to the TCP-Server.
			connection = new RemoteConnection();

			try{
				connection.connect(user + ":" + password + "@" + server
						+ ":" + port + "/" + id);
			}catch(Exception e){
				System.out.println("Cannot connect to server!");
				System.exit(1);
			}
					
			System.out.println("Connected");

			deviceAsync = new RemoteDevice(connection);
		} else {
			if (deviceParameter != null) {
				final OperationQueue queue = new PausableExecutorOperationQueue();
				Device<?> device = null;
				if (deviceParameter.equals("jennec")) {
					// Connect to the local jennec-device.
					SerialPortConnection jennicConnection = new iSenseSerialPortConnection();
					jennicConnection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new JennicDevice(jennicConnection);
					jennicConnection.connect(port);
				} else if (deviceParameter.equals("pacemate")) {
					// Connect to the local pacemate-device.
					SerialPortConnection pacemateConnection = new iSenseSerialPortConnection();
					pacemateConnection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new PacemateDevice(pacemateConnection);
					pacemateConnection.connect(port);
				} else if (deviceParameter.equals("telosb")) {
					// Connect to the local telosb-device
					SerialPortConnection telosbConnection = new TelosbSerialPortConnection();
					telosbConnection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new TelosbDevice(telosbConnection);
					telosbConnection.connect(port);
				}else if(deviceParameter.equals("mock")){
					final MockConnection connection = new MockConnection();
					device = new MockDevice(connection);
					connection.connect("MockPort");
					System.out.println("Connected");
				}
				deviceAsync = new QueuedDeviceAsync(queue, device);
			}
		}
	}

	/**
	 * Send.
	 * Sends the message to the device and handles the response.
	 * 
	 * @param message
	 *            the message
	 */
	public void send(String message) {
		MessagePacket packet = new MessagePacket(messageType, hexStringToByteArray(message));
		deviceAsync.send(packet, 100000, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Sending is starting now...");
			}
			
			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Sending the message: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Message sent");
				sent = true; // for tests
				if(connection != null){
					connection.shutdown(false);
				}
				System.exit(1);
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println("Error while sending the message.");
				if(connection != null){
					connection.shutdown(false);
				}
				System.exit(1);
			}
		});
		System.out.println("Sending process was added to the queue.");
	}
	
	/**
	 * Converts a hex-String to a byte array to send this as message to the device.
	 * @param s
	 * @return data, the byte array
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}