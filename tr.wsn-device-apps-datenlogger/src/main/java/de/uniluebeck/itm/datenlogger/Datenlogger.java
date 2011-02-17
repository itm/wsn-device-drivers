package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Capability;
import model.DisableLink;
import model.DisableNode;
import model.EnableLink;
import model.EnableNode;
import model.Link;
import model.LinkDefaults;
import model.Node;
import model.NodeDefaults;
import model.Origin;
import model.Position;
import model.Rssi;
import model.Scenario;
import model.Setup;
import model.Timeinfo;
import model.Trace;
import viewer.CreateXML;

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
 * Class Datenlogger.
 * Functions to registrate a Datenlogger on a sensornode 
 * and print the messages on the console or in a file.
 */
public class Datenlogger {
	
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
	 * Instantiates a new datenlogger.
	 */
	public Datenlogger(){
	}
	
	/**
	 * Getter/Setters
	 */
	public String getPort(){
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
	
	public void setDevice(String device){
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
	 *
	 * @param klammer_filter the klammer_filter
	 * @return the predicate
	 */
	public Predicate<CharSequence> parse_klammer_filter(String klammer_filter){
		Stack<Predicate<CharSequence>> ausdruecke = new Stack<Predicate<CharSequence>>();
		Stack<String> operatoren = new Stack<String>();
		String ausdruck = "";
		for(int i = 0; i < klammer_filter.length(); i++){
			String character = Character.toString(klammer_filter.charAt(i));
			if(character.equals("|")){
				operatoren.push("or");
			}else if(character.equals("&")){
				operatoren.push("and");
			}else if(character.equals("(")){
				//do nothing
			}else if(character.equals(")")){
				if(!ausdruck.equals("")){
					Predicate<CharSequence> predicate = new Klammer_Predicate(ausdruck);
					ausdruecke.push(predicate);
					ausdruck = "";
				}else{
					Predicate<CharSequence> erster_ausdruck = ausdruecke.pop();
					Predicate<CharSequence> zweiter_ausdruck = ausdruecke.pop();
					String operator = operatoren.pop();
					if(operator.equals("or")){
						Predicate<CharSequence> ergebnis = or(erster_ausdruck, zweiter_ausdruck);
						ausdruecke.push(ergebnis);
					}else if(operator.equals("and")){
						Predicate<CharSequence> ergebnis = and(erster_ausdruck, zweiter_ausdruck);
						ausdruecke.push(ergebnis);
					}
				}
			}else{
				ausdruck = ausdruck + character;
			}			
		}
		while(operatoren.size() != 0){
			Predicate<CharSequence> erster_ausdruck = ausdruecke.pop();
			Predicate<CharSequence> zweiter_ausdruck = ausdruecke.pop();
			String operator = operatoren.pop();
			if(operator.equals("or")){
				Predicate<CharSequence> ergebnis = or(erster_ausdruck, zweiter_ausdruck);
				ausdruecke.push(ergebnis);
			}else if(operator.equals("and")){
				Predicate<CharSequence> ergebnis = and(erster_ausdruck, zweiter_ausdruck);
				ausdruecke.push(ergebnis);
			}
		}	
		return ausdruecke.pop();
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
			connection.connect("MockPort");
			System.out.println("Connected");
			
			deviceAsync = new QueuedDeviceAsync(queue, device);
		}
	}

	/**
	 * Startlog.
	 */
	public void startlog(){
		started = true;
		
		if(location != null){
			try {
				writer = new FileWriter(location);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Message packet listener added");
		listener = new MessagePacketListener() {
			@Override
			public void onMessagePacketReceived(
					de.uniluebeck.itm.devicedriver.event.MessageEvent<MessagePacket> event) {
				String incoming_data = new String(event.getMessage().getContent());
				incoming_data = incoming_data.substring(1);
				//Filter
				boolean matches = false;
				
				//(Datatype, Begin, Value)-Filter
				if(brackets_filter != null){
					matches = parse_klammer_filter(brackets_filter).apply(incoming_data);
				}
					
				//Reg-Ex-Filter
				//"[+-]?[0-9]+"
				if(regex_filter != null){
					Pattern p = Pattern.compile(regex_filter);
					Matcher m = p.matcher(incoming_data);
					matches = m.matches();
				}	
				
				if(!matches){
					if(location != null){
						try {
							byte[] bytes = event.getMessage().getContent();
							
							if(output.equals("1")){
								writer.write(StringUtils.toHexString(bytes));
							}else{
								writer.write(incoming_data);
								writer.write("\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else{
						byte[] bytes = event.getMessage().getContent();
						
						if(output.equals("1")){
							System.out.println(StringUtils.toHexString(bytes));
						}
						else{
							System.out.println(incoming_data);
						}
					}
				}
				else{
					System.out.println("Data was filtered.");
				}
			}
		};
		deviceAsync.addListener(listener, PacketType.LOG);
	}	
	
	/**
	 * Stoplog.
	 */
	public void stoplog(){
		deviceAsync.removeListener(listener);
		if(location != null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		started = false;
		System.out.println("\nEnd of Logging.");
	}
	
	/**
	 * Add_klammer_filter.
	 *
	 * @param filter the filter
	 */
	public void add_klammer_filter(String filter){
		brackets_filter = brackets_filter + filter;
		System.out.println("Filter added");
	}
	
	/**
	 * Add_regex_filter.
	 *
	 * @param filter the filter
	 */
	public void add_regex_filter(String filter){
		regex_filter = regex_filter + filter;
		System.out.println("Filter added");
	}
	
	/**
	 * Write to xml file.
	 */
	public void writeToXmlFile(){
		//Read the xml file
        CreateXML create = new CreateXML();

        List<Node> nodeList = new ArrayList<Node>();
        List<Link> edgeList = new ArrayList<Link>();
        List<Capability> capList = new ArrayList<Capability>();

        // --- N1 ---
        //Add Node Capabilities

        Capability cap1 = new Capability("urn:wisebed:node:capability:temp", "integer", "lux", 2);
        Capability cap2 = new Capability("urn:wisebed:node:capability:time", "Float", "lux", 0);
        capList.add(cap1);
        capList.add(cap2);

        /**
         * Create all the required elemets for the setup section of a wiseml file.
         */
        Position position = new Position(1.23, 1.56, 1.77);
        //Data data = new Data("urn:wisebed:node:capability:time");

        Node node1 = new Node("urn:wisebed:node:tud:M4FTR", "gw1", "123.456.79", "ein Knoten", capList);
        nodeList.add(node1);

        Rssi rssi = new Rssi("decimal", "dBam", "-120");

        Link edge = new Link("urn:wisebed:node:tud:330006", "urn:wisebed:node:tud:330009",
                "true", "false", rssi, capList);
        edgeList.add(edge);

        Origin origin = new Origin(position, 5, 0);
        Timeinfo time = new Timeinfo("9/7/2009", "19/12/2010", "seconds");
        NodeDefaults ndefault = new NodeDefaults("node", node1);
        LinkDefaults ldefault = new LinkDefaults("link", edge);
        Setup setup = new Setup(origin, time, "cubic", "wiseml example", ndefault, ldefault, nodeList, edgeList);

        /**
         *  Create all the required elemets for the scenario section of a wiseml file.
         */

        EnableNode enable = new EnableNode("urn:wisebed:node:tud:M4FTR");
        EnableLink enablel = new EnableLink("urn:wisebed:node:tud:33000", "urn:wisebed:node:tud:330009");
        DisableNode disable = new DisableNode("urn:wisebed:node:tud:M4FTR");
        DisableLink disablel = new DisableLink("urn:wisebed:node:tud:33000", "urn:wisebed:node:tud:33000");
        Scenario scenario = new Scenario("scenario_1", "23/09/09", enable, disable, enablel, disablel, node1);
        Trace trace = new Trace("Trace_1","2/5/09", node1, edge);

        /**
         * Create the final wiseml file.
         */
        create.writeXML("test.xml", setup, scenario, trace);
	}
}
