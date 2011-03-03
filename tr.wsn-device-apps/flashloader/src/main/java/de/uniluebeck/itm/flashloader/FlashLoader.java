package de.uniluebeck.itm.flashloader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.io.Files;

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
import de.uniluebeck.itm.devicedriver.telosb.TelosbSerialPortConnection;
import de.uniluebeck.itm.tcp.client.RemoteConnection;
import de.uniluebeck.itm.tcp.client.RemoteDevice;

/**
 * Class FlashLoader. Functions to flash a device, read/write the Mac-Adress
 * or reset the device.
 */
public class FlashLoader {

	private static Log log = LogFactory.getLog(FlashLoader.class);
	private String port;
	private String server;
	private String user;
	private String password;
	private String device_parameter;
	private DeviceAsync deviceAsync;
	private String id;
	private int timeout = 300000;
	private int flash_process = 0;

	private boolean flashed = false; // for the test-class
	private String current_mac_adress; // for the test-class
	private boolean resetet = false; // for the test-class

	/**
	 * Instantiates a new flash loader.
	 */
	public FlashLoader(String port, String server, String user, String password, String device_parameter, String id, String timeout) {
		this.port = port;
		this.server = server;
		this.user = user;
		this.password = password;
		this.device_parameter = device_parameter;
		this.id = id;
		if(timeout != null){
			this.timeout = Integer.parseInt(timeout);
		}
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
				}
				deviceAsync = new QueuedDeviceAsync(queue, device);
			}
		}
	}

	/**
	 * Flash the given file on the device
	 * and handle the response.
	 * 
	 * @param file
	 *            the file
	 */
	public void flash(String file) {
		byte[] image = null;
		if(device_parameter != null || server != null){
			try {
				if(file != null){
					image = Files.toByteArray(new File(file));
				}
				else{
					image = "Mock Device".getBytes();
				}
			} catch (IOException e) {
				log.error("Error while reading file.");
				System.exit(1);
			}
		}
		else{
			image = "Mock Device".getBytes();
		}
		deviceAsync.program(image, timeout, new AsyncAdapter<Void>() {
			@Override
			public void onExecute() {
				System.out.println("Flashing is starting now...");
			}

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("The Device has been flashed.");
				flashed = true; // for tests
			}

			@Override
			public void onFailure(Throwable throwable) {
				log.error("Error while flashing the device.");
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

		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent
						+ "%");
			}

			public void onSuccess(MacAddress result) {
				System.out.println("Mac Address: " + result.toString());
				current_mac_adress = result.toString();
			}

			public void onFailure(Throwable throwable) {
				log.error("Error while reading the mac address.");
				System.exit(1);
			}
		};
		deviceAsync.readMac(10000, callback);
	}

	/**
	 * Writemac writes the Mac-Adress of the device.
	 * 
	 * @param macAddress
	 *            the mac address
	 */
	public void writemac(MacAddress macAddress) {
		System.out.println("Setting Mac Address");
		deviceAsync.writeMac(macAddress, timeout,
				new AsyncAdapter<Void>() {

					@Override
					public void onProgressChange(float fraction) {
						final int percent = (int) (fraction * 100.0);
						System.out.println("Writing mac address progress: "
								+ percent + "%");
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("Mac Address written");
					}

					@Override
					public void onFailure(Throwable throwable) {
						log.error("Error while writing the mac address.");
						throwable.printStackTrace();
						System.exit(1);
					}
				});
	}

	/**
	 * Resets the device.
	 */
	public void reset() {
		System.out.println("Reset");
		deviceAsync.reset(timeout, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reset the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Device has been reseted");
				resetet = true; //for tests
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println("Error while reseting the device");
				throwable.printStackTrace();
				System.exit(1);
			}
		});
	}
	
}