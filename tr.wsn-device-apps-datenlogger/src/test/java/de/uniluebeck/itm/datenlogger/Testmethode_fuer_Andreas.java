package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;
import de.uniluebeck.itm.tcp.client.RemoteConnection;
import de.uniluebeck.itm.tcp.client.RemoteDevice;

public class Testmethode_fuer_Andreas {
	
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

	public static void main(String[] args) throws IOException {
		final Datenlogger datenlogger = new Datenlogger();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = in.readLine();
		
		if(input.equals("server")){
			final RemoteConnection connection = new RemoteConnection();
			
			connection.connect("1:testUser:testPassword@localhost:8080");
			System.out.println("Connected");
			
			datenlogger.deviceAsync = new RemoteDevice(connection);
		}
		if(input.equals("lokal")){
			final OperationQueue queue = new PausableExecutorOperationQueue();
			final MockConnection connection = new MockConnection();
			Device device = new MockDevice(connection);
			
			connection.connect("MockPort");
			System.out.println("Connected");
			
			datenlogger.deviceAsync = new QueuedDeviceAsync(queue, device);
			
			System.out.println("Message packet listener added");
			datenlogger.deviceAsync.addListener(new MessagePacketListener() {
				public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
					System.out.println("Message: " + new String(event.getMessage().getContent()));
				}
			}, PacketType.LOG);
		}

		
		datenlogger.gestartet = true;
		
		System.out.println("Message packet listener added");
		datenlogger.listener = new MessagePacketListener() {
			@Override
			public void onMessagePacketReceived(
					de.uniluebeck.itm.devicedriver.event.MessageEvent<MessagePacket> event) {
				String erhaltene_Daten = new String(event.getMessage().getContent());
				System.out.println("Daten erhalten");
				//Filtern
				boolean matches = false;
				
				//(Datentyp, Beginn, Wert)-Filter
				if(datenlogger.klammer_filter != null){
					matches = datenlogger.parse_klammer_filter(datenlogger.klammer_filter).apply(erhaltene_Daten);
				}
					
				//Reg-Ausdruck-Filter
				//"[+-]?[0-9]+"
				if(datenlogger.regex_filter != null){
					Pattern p = Pattern.compile(datenlogger.regex_filter);
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
		datenlogger.deviceAsync.addListener(datenlogger.listener, PacketType.LOG);
	}
}
