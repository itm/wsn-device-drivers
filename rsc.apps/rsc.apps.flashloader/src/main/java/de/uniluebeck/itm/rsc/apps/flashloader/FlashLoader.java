package de.uniluebeck.itm.rsc.apps.flashloader;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import de.uniluebeck.itm.rsc.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.rsc.drivers.core.ConnectionListener;
import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
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
import de.uniluebeck.itm.rsc.remote.client.RemoteConnection;
import de.uniluebeck.itm.rsc.remote.client.RemoteDevice;

/**
 * Class FlashLoader. Functions to flash a device, read/write the Mac-Adress or
 * reset the device.
 */
public class FlashLoader {

	private String port;
	private String server;
	private String username;
	private String password;
	private String deviceParameter;
	private DeviceAsync deviceAsync;
	private String id;
	private int timeout = 300000;
	private RemoteConnection connection;

	/**
	 * Instantiates a new flash loader.
	 * 
	 * @param port
	 * @param server
	 * @param username
	 * @param password
	 * @param deviceParameter
	 * @param id
	 * @param timeout
	 */
	public FlashLoader(final String port, final String server,
			final String username, final String password,
			final String deviceParameter, final String id, final String timeout) {
		this.port = port;
		this.server = server;
		this.username = username;
		this.password = password;
		this.deviceParameter = deviceParameter;
		this.id = id;
		if (timeout != null) {
			this.timeout = Integer.parseInt(timeout);
		}
	}

	/**
	 * Gets the RemoteConnection, when the client is connected to the server.
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
				connection.connect(username + ":" + password + "@" + server
						+ ":" + port + "/" + id);
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
	 * Flash the given file on the device and handle the response.
	 * 
	 * @param file
	 */
	public void flash(final String file) {
		byte[] image = null;
		try {
			image = Files.toByteArray(new File(file));
		} catch (IOException e) {
			System.out.println("Error while reading file.");
			System.exit(1);
		}

		// Adds a flash-process to the waiting-queue and defines a callback to
		// react to incoming status-messages
		deviceAsync.program(image, timeout, new AsyncAdapter<Void>() {
			@Override
			public void onExecute() {
				System.out.println("Flashing is starting now...");
			}

			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(final Void result) {
				System.out.println("The Device has been flashed.");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}

			@Override
			public void onFailure(final Throwable throwable) {
				System.out.println("Error while flashing the device.");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}
		});
		System.out.println("Flashing process was added to the queue.");
	}

	/**
	 * Readmac reads the Mac-Adress of the device.
	 */
	public void readmac() {
		System.out.println("Reading mac address...");

		// defines a callback to react to incoming status-messages
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {

			@Override
			public void onExecute() {
				System.out.println("Read mac address is starting now...");
			}

			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent
						+ "%");
			}

			public void onSuccess(final MacAddress result) {
				System.out.println("Mac Address: " + result.toString());
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}

			public void onFailure(final Throwable throwable) {
				System.out.println("Error while reading the mac address.");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}
		};
		// adds the readMac-process to the queue
		deviceAsync.readMac(timeout, callback);
		System.out.println("Reading process was added to the queue.");
	}

	/**
	 * Writemac writes the Mac-Adress of the device.
	 * 
	 * @param macAddress
	 */
	public void writemac(final MacAddress macAddress) {
		System.out.println("Setting Mac Address");
		// adds the writeMac-process to the queue and defines a callback to
		// react to incoming status-messages
		deviceAsync.writeMac(macAddress, timeout, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Write mac address is starting now...");
			}

			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing mac address progress: " + percent
						+ "%");
			}

			@Override
			public void onSuccess(final Void result) {
				System.out.println("Mac Address written");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}

			@Override
			public void onFailure(final Throwable throwable) {
				System.out.println("Error while writing the mac address.");
				throwable.printStackTrace();
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}
		});
		System.out.println("Writing process was added to the queue.");
	}

	/**
	 * Resets the device.
	 */
	public void reset() {
		System.out.println("Reset");
		// adds the reset-process to the queue and defines a callback to react
		// to incoming status-messages
		deviceAsync.reset(timeout, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Reset is starting now...");
			}

			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reset the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(final Void result) {
				System.out.println("Device has been reseted");
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}

			@Override
			public void onFailure(final Throwable throwable) {
				System.out.println("Error while reseting the device");
				throwable.printStackTrace();
				if (connection != null) {
					connection.shutdown(false);
				}
				System.exit(1);
			}
		});
		System.out.println("Reset process was added to the queue.");
	}

}