package de.uniluebeck.itm.messenger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private static Log log = LogFactory.getLog(Messenger.class);
	private String port;
	private String server;
	private String user;
	private String password;
	private String device_parameter;
	private DeviceAsync deviceAsync;
	private String id;
	private byte message_type;
	private boolean sent = false; // for the test-class

	/**
	 * Instantiates a new messenger.
	 */
	public Messenger(String port, String server, String user, String password, String device, String id, int message_type) {
		this.port = port;
		this.server = server;
		this.user = user;
		this.password = password;
		this.device_parameter = device;
		this.id = id;
		this.message_type = (byte)message_type;
	}
	
	/**
	 * Connect. Method to connect to the tcp-server or to a local sensornode.
	 */
	public void connect() {
		if (server != null) {
			// Connect to the TCP-Server.
			RemoteConnection connection = new RemoteConnection();

			connection.connect(user + ":" + password + "@" + server
					+ ":" + port + "/" + id);
			System.out.println("Connected");

			deviceAsync = new RemoteDevice(connection);
		} else {
			if (device_parameter != null) {
				final OperationQueue queue = new PausableExecutorOperationQueue();
				Device<?> device = null;
				if (device_parameter.equals("jennec")) {
					// Connect to the local jennec-device.
					SerialPortConnection jennic_connection = new iSenseSerialPortConnection();
					jennic_connection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new JennicDevice(jennic_connection);
					jennic_connection.connect(port);
				} else if (device_parameter.equals("pacemate")) {
					// Connect to the local pacemate-device.
					SerialPortConnection pacemate_connection = new iSenseSerialPortConnection();
					pacemate_connection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new PacemateDevice(pacemate_connection);
					pacemate_connection.connect(port);
				} else if (device_parameter.equals("telosb")) {
					// Connect to the local telosb-device
					SerialPortConnection telosb_connection = new TelosbSerialPortConnection();
					telosb_connection.addListener(new ConnectionListener() {
						@Override
						public void onConnectionChange(ConnectionEvent event) {
							if (event.isConnected()) {
								System.out
										.println("Connection established with port "
												+ event.getUri());
							}
						}
					});
					device = new TelosbDevice(telosb_connection);
					telosb_connection.connect(port);
				}else if(device_parameter.equals("mock")){
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
		MessagePacket packet = new MessagePacket(message_type, hexStringToByteArray(message));
		deviceAsync.send(packet, 100000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Sending the message: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Message sent");
				sent = true; // for tests
			}

			@Override
			public void onFailure(Throwable throwable) {
				log.error("Error while sending the message.");
				System.exit(1);
			}
		});
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