package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

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

public class Datenlogger {
	
	String port;
	String server;
	String klammer_filter;
	String regex_filter;
	String location;
	String user;
	String passwort;
	boolean gestartet = false;
	String device_parameter;
	DeviceAsync deviceAsync;
	MessagePacketListener listener;

	public Datenlogger(){
	}

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
	
	public void setDevice(String device){
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

	public boolean isGestartet() {
		return gestartet;
	}

	public void setGestartet(boolean gestartet) {
		this.gestartet = gestartet;
	}

	public void setKlammer_filter(String klammer_filter) {
		this.klammer_filter = klammer_filter;
	}

	public void setRegex_filter(String regex_filter) {
		this.regex_filter = regex_filter;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void getloggers(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
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
		}
	}
	
	public void startlog(){
		gestartet = true;
		
		System.out.println("Message packet listener added");
		listener = new MessagePacketListener() {
			@Override
			public void onMessagePacketReceived(
					de.uniluebeck.itm.devicedriver.event.MessageEvent<MessagePacket> event) {
				String erhaltene_Daten = new String(event.getMessage().getContent());
				System.out.println("Daten erhalten");
				//Filtern
				boolean matches = false;
				
				//(Datentyp, Beginn, Wert)-Filter
				if(klammer_filter != null){
					matches = parse_klammer_filter(klammer_filter).apply(erhaltene_Daten);
				}
					
				//Reg-Ausdruck-Filter
				//"[+-]?[0-9]+"
				if(regex_filter != null){
					Pattern p = Pattern.compile(regex_filter);
					Matcher m = p.matcher(erhaltene_Daten);
					matches = m.matches();
				}	
				
				if(!matches){
					System.out.println("Message: " + erhaltene_Daten);
					//writeToXmlFile();
				}
				else{
					System.out.println("Daten wurden gefiltert.");
				}
			}
		};
		deviceAsync.addListener(listener, PacketType.LOG);
	}	
	
	public void stoplog(){
		deviceAsync.removeListener(listener);
		gestartet = false;
		System.out.println("\nDas Loggen des Knotens wurde beendet.");
	}
	
	public void add_klammer_filter(String filter){
		klammer_filter = klammer_filter + filter;
		System.out.println("Filter hinzugefuegt");
	}
	
	public void add_regex_filter(String filter){
		regex_filter = regex_filter + filter;
		System.out.println("Filter hinzugefuegt");
	}
	
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
