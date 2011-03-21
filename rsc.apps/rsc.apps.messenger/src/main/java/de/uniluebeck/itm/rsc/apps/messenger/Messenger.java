package de.uniluebeck.itm.rsc.apps.messenger;

import de.uniluebeck.itm.rsc.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.rsc.drivers.core.ConnectionListener;
import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.rsc.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.rsc.drivers.core.mockdevice.MockConnection;
import de.uniluebeck.itm.rsc.drivers.core.mockdevice.MockDevice;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseSerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.jennic.JennicDevice;
import de.uniluebeck.itm.rsc.drivers.pacemate.PacemateDevice;
import de.uniluebeck.itm.rsc.drivers.telosb.TelosbDevice;
import de.uniluebeck.itm.rsc.drivers.telosb.TelosbSerialPortConnection;
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
	private int timeout = 300000;
	private RemoteConnection connection;

	/**
	 * Instantiates a new messenger.
	 *
	 * @param port the port
	 * @param server the server
	 * @param user the user
	 * @param password the password
	 * @param device the device
	 * @param id the id
	 * @param messageType the message type
	 */
	public Messenger(final String port, final String server, final String user, 
			final String password, final String device, final String id, 
			final int messageType, final String timeout) {
		this.port = port;
		this.server = server;
		this.user = user;
		this.password = password;
		this.deviceParameter = device;
		this.id = id;
		this.messageType = (byte) messageType;
		if (timeout != null) {
			this.timeout = Integer.parseInt(timeout);
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public RemoteConnection getConnection() {
		return connection;
	}

	/**
	 * Method to connect to the tcp-server or to a local sensornode.
	 */
	public void connect() {
		if (server != null) {
			// Connect to the TCP-Server.
			connection = new RemoteConnection();

			try {
				connection.connect(user + ":" + password + "@" + server + ":"
						+ port + "/" + id);
			} catch (Exception e) {
				System.out.println("Cannot connect to server!");
				System.exit(1);
			}
			System.out.println("Connected");

			deviceAsync = new RemoteDevice(connection);
		} else {
			if (deviceParameter != null) {
				final OperationQueue queue = new PausableExecutorOperationQueue();
				Device<?> device = null;
				if (deviceParameter.equals("jennic")) {
					// Connect to the local jennic-device.
					SerialPortConnection jennicConnection = null;
					try {
						jennicConnection = new iSenseSerialPortConnection();
					} catch (java.lang.ExceptionInInitializerError e) {
						System.out.println("Could not connect to device!");
						System.exit(1);
					}
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
					SerialPortConnection pacemateConnection = null;
					try {
						pacemateConnection = new iSenseSerialPortConnection();
					} catch (java.lang.ExceptionInInitializerError e) {
						System.out.println("Could not connect to device!");
						System.exit(1);
					}
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
					SerialPortConnection telosbConnection = null;
					try {
						telosbConnection = new TelosbSerialPortConnection();
					} catch (java.lang.ExceptionInInitializerError e) {
						System.out.println("Could not connect to device!");
						System.exit(1);
					}
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
				} else if (deviceParameter.equals("mock")) {
					// Connect to the mock-device for tests
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
	 * Send. Sends the message to the device and handles the response.
	 * @param message
	 *            the message
	 */
	public void send(final String message) {
		MessagePacket packet = new MessagePacket(messageType,
				hexStringToByteArray(message));
		deviceAsync.send(packet, timeout, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Sending is starting now...");
			}

			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Sending the message: " + percent + "%");
			}

			@Override
			public void onSuccess(final Void result) {
				System.out.println("Message sent");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}

			@Override
			public void onFailure(final Throwable throwable) {
				System.out.println("Error while sending the message.");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}
		});
		System.out.println("Sending process was added to the queue.");
	}

	/**
	 * Converts a hex-String to a byte array to send this as message to the
	 * device.
	 *
	 * @param s the s
	 * @return data, the byte array
	 */
	public static byte[] hexStringToByteArray(final String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}