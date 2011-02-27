package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Predicate;

import de.uniluebeck.itm.tr.util.*;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
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
 * Class Datalogger. Functions to registrate a datalogger on a sensornode and
 * print the messages on the console or in a file.
 */
public class Datalogger {

	private static Log log = LogFactory.getLog(Datalogger.class);
	private String port;
	private String server;
	private String brackets_filter;
	private String regex_filter;
	private String location;
	private String user;
	private String password;
	private boolean started = false;
	private String device_parameter;
	private DeviceAsync deviceAsync;
	private MessagePacketListener listener;
	private FileWriter writer;
	private String output;
	private String id;

	/**
	 * Instantiates a new datalogger.
	 */
	public Datalogger() {
	}

	/**
	 * Getter/Setters
	 */
	public String getPort() {
		return port;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String getServer() {
		return server;
	}

	public String getKlammer_filter() {
		return brackets_filter;
	}

	public String getRegex_filter() {
		return regex_filter;
	}

	public String getLocation() {
		return location;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDevice_parameter() {
		return device_parameter;
	}

	public DeviceAsync getDeviceAsync() {
		return deviceAsync;
	}

	public MessagePacketListener getListener() {
		return listener;
	}

	public FileWriter getWriter() {
		return writer;
	}

	public String getOutput() {
		return output;
	}

	public String getId() {
		return id;
	}

	public void setDevice(String device) {
		this.device_parameter = device;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setStartet(boolean started) {
		this.started = started;
	}

	public void setKlammer_filter(String klammer_filter) {
		this.brackets_filter = klammer_filter;
	}

	public void setRegex_filter(String regex_filter) {
		this.regex_filter = regex_filter;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Parse_klammer_filter.
	 * Uses a stack to parse the brackets-filter 
	 * for example: 
	 * ((Datatype, Begin, Value)&(Datatype, Begin, Value))|(Datatype, Begin, Value)
	 * 
	 * @param klammer_filter
	 *            the klammer_filter
	 * @return the predicate
	 */
	public Predicate<CharSequence> parse_brackets_filter(String bracket_filter) {
		Stack<Predicate<CharSequence>> expressions = new Stack<Predicate<CharSequence>>();
		Stack<String> operators = new Stack<String>();
		String expression = "";
		for (int i = 0; i < bracket_filter.length(); i++) {
			String character = Character.toString(bracket_filter.charAt(i));
			if (character.equals("|")) {
				operators.push("or");
			} else if (character.equals("&")) {
				operators.push("and");
			} else if (character.equals("(")) {
				// do nothing
			} else if (character.equals(")")) {
				if (!expression.equals("")) {
					Predicate<CharSequence> predicate = new Brackets_Predicate(
							expression);
					expressions.push(predicate);
					expression = "";
				} else {
					Predicate<CharSequence> first_expression = expressions.pop();
					Predicate<CharSequence> second_expression = expressions.pop();
					String operator = operators.pop();
					if (operator.equals("or")) {
						Predicate<CharSequence> result = or(first_expression,
								second_expression);
						expressions.push(result);
					} else if (operator.equals("and")) {
						Predicate<CharSequence> result = and(first_expression,
								second_expression);
						expressions.push(result);
					}
				}
			} else {
				expression = expression + character;
			}
		}
		while (operators.size() != 0) {
			Predicate<CharSequence> first_expression = expressions.pop();
			Predicate<CharSequence> second_expression = expressions.pop();
			String operator = operators.pop();
			if (operator.equals("or")) {
				Predicate<CharSequence> result = or(first_expression,
						second_expression);
				expressions.push(result);
			} else if (operator.equals("and")) {
				Predicate<CharSequence> result = and(first_expression,
						second_expression);
				expressions.push(result);
			}
		}
		return expressions.pop();
	}

	/**
	 * Connect.
	 * Method to connect to the tcp-server or to a local Sensornode.
	 */
	public void connect() {
		if (server != null) {
			//Connect to the TCP-Server.
			final RemoteConnection connection = new RemoteConnection();

			connection.connect(id + ":" + user + ":" + password + "@" + server
					+ ":" + port);
			System.out.println("Connected");

			deviceAsync = new RemoteDevice(connection);
		} else {

			final OperationQueue queue = new PausableExecutorOperationQueue();
			final MockConnection connection = new MockConnection();
			Device device = new MockDevice(connection);

			if (device_parameter != null) {
				if (device_parameter.equals("jennec")) {
					//Connect to the local jennec-device.
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
					//Connect to the local pacemate-device.
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
					//Connect to the local telosb-device
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
			}
			//there is no device-parameter oder server-parameter, so connect to the mock-device
			connection.connect("MockPort");
			System.out.println("Connected");

			deviceAsync = new QueuedDeviceAsync(queue, device);
		}
	}

	/**
	 * Startlog.
	 * Registers a message packet listener on the connected device
	 * and handles the incoming data.
	 */
	public void startlog() {
		started = true;

		if (location != null) {		//data shall be written in a text-file.
			try {
				writer = new FileWriter(location);
			} catch (IOException e) {
				log.error("Error while creating the writer.");
			}
		}

		System.out.println("Message packet listener added");
		listener = new MessagePacketListener() {
			@Override
			public void onMessagePacketReceived(
					de.uniluebeck.itm.devicedriver.event.MessageEvent<MessagePacket> event) {
				String incoming_data = new String(event.getMessage()
						.getContent());
				incoming_data = incoming_data.substring(1);
				
				// Filter-matching
				boolean matches = false;
				// (Datatype, Begin, Value)-Filter
				if (brackets_filter != null) {
					matches = parse_brackets_filter(brackets_filter).apply(
							incoming_data);
				}
				// Reg-Ex-Filter
				// "[+-]?[0-9]+"
				if (regex_filter != null) {
					Pattern p = Pattern.compile(regex_filter);
					Matcher m = p.matcher(incoming_data);
					matches = m.matches();
				}

				if (!matches) {		//if the filters not match the incoming data => log it.
					if (location != null) {		//write to text-file
						try {
							byte[] bytes = event.getMessage().getContent();

							if (output.equals("hex")) {	//encoding of the data shall be hex.
								writer.write(StringUtils.toHexString(bytes));
							} else {
									writer.write(incoming_data);
									writer.write("\n");
							}
						} catch (IOException e) {
							log.error("Error while writing the data.");
						}
					} else {	//output on terminal
						byte[] bytes = event.getMessage().getContent();

						if (output.equals("hex")) {	//encoding of the data shall be hex.
							System.out.println(StringUtils.toHexString(bytes));
						} else {
								System.out.println(incoming_data);
						}
					}
				} else {
					System.out.println("Data was filtered.");
				}
			}
		};
		deviceAsync.addListener(listener, PacketType.LOG);
	}

	/**
	 * Stoplog.
	 * Remove the registered Listener and close the writer.
	 */
	public void stoplog() {
		deviceAsync.removeListener(listener);
		if (location != null) {
			try {
				writer.close();
			} catch (IOException e) {
				log.error("Error while closing the writer.");
			}
		}
		started = false;
		System.out.println("\nEnd of Logging.");
	}

	/**
	 * Add_klammer_filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void add_klammer_filter(String filter) {
		brackets_filter = brackets_filter + filter;
		System.out.println("Filter added");
	}

	/**
	 * Add_regex_filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void add_regex_filter(String filter) {
		regex_filter = regex_filter + filter;
		System.out.println("Filter added");
	}
	
}
