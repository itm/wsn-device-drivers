package de.uniluebeck.itm.commandlineTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;

public class CommandlineTool {
	
	private static double version = 0.1;
	
	public static void main(String[] args) throws IOException {
		// create Options object
		Option help_option = new Option( "help", "print this message" );
		Option version_option = new Option( "version", "print the version information" );
		
		Options options = new Options();
		
		options.addOption(help_option);
		options.addOption(version_option);
		
		// add options for Messenger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("message", true, "Die Nachricht, die verschickt werden soll in Hex-Code");

		// add options for Datenlogger
		options.addOption("location", true, "Ausgabeziel der Daten, die geloggt werden");
		options.addOption("filters", true, "Kombination der Filtertypen: Regular Expression-Filter, (Datentyp,Beginn,Wert)-Filter");

		// add options for FlashLoader
		options.addOption("file", true, "Enthält das Programm, das geflasht werden soll");
		
		// add options for Meta-Service
		options.addOption("id", true, "Enthält die ID, nach der gesucht werden soll");
		options.addOption("microcontroller", true, "Enthält den Mircrocontroller, nach dem gesucht werden soll");
		options.addOption("sensor", true, "Enthält den Sensor, nach dem gesucht werden soll");
		
		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		String last_line = buffer.readLine();
		args = last_line.split(" ");
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Diese Option gibt es nicht.");
		}
		while(!(last_line.equals("exit"))){
			
			if(cmd != null){
				//standard-options
				if(cmd.hasOption("help")){
					System.out.println("Aufrufbeispiele:");
					System.out.println("Messenger: send -message 0a 3f 41 -port 141.83.1.546:1282");
					System.out.println("Datenlogger: startlog -filter 0a, 0b, 54 -location filename.txt -port 141.83.1.546:1282");
					System.out.println("Flashloader: flash -port x -file programm.bin");
					System.out.println("Meta-Daten Service: metadata -id 123");
					System.out.println("");
					formatter.printHelp("help", options);
				}
				if(cmd.hasOption("version")){
					System.out.println(version);
				}
				
				//der Messenger
				if(args[0].equals("send")) {
					System.out.println("starte Messenger...");
					
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					String message = cmd.getOptionValue("message");
					
					Messenger messenger = new Messenger();
					messenger.setPort(port);
					messenger.setServer(server);
					messenger.send(message);							
				}
				
				//der Datenlogger
				if(args[0].equals("getloggers")) {
					System.out.println("starte Datenlogger...");
		
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					
					Datenlogger datenlogger = new Datenlogger();
					datenlogger.setPort(port);
					datenlogger.setServer(server);
					datenlogger.getloggers();
					
				}else if(args[0].equals("startlog")) {
					System.out.println("starte Datenlogger...");
					
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					String filters = cmd.getOptionValue("filters");
					String location = cmd.getOptionValue("location");
					
					Datenlogger datenlogger = new Datenlogger();
					datenlogger.setPort(port);
					datenlogger.setServer(server);
					datenlogger.setFilters(filters);
					datenlogger.setLocation(location);
					datenlogger.startlog();
					
				}else if(args[0].equals("stoplog")) {
					System.out.println("starte Datenlogger...");
					
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					
					Datenlogger datenlogger = new Datenlogger();
					datenlogger.setPort(port);
					datenlogger.setServer(server);
					datenlogger.stoplog();
					
				}else if(args[0].equals("addfilter")) {
					System.out.println("starte Datenlogger...");
					
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					
					Datenlogger datenlogger = new Datenlogger();
					datenlogger.setPort(port);
					datenlogger.setServer(server);
					datenlogger.addfilter();
					
				}
				
				//der FlashLoader
				if(args[0].equals("flash")) {
					System.out.println("starte FlashLoader...");
					
					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					String file = cmd.getOptionValue("file");
					
					FlashLoader flashLoader = new FlashLoader();
					flashLoader.setPort(port);
					flashLoader.setServer(server);
					flashLoader.setFile(file);
					flashLoader.flash();	
					
				}else if(args[0].equals("readmac")) {
					System.out.println("starte FlashLoader...");
					
					FlashLoader flashLoader = new FlashLoader();
					flashLoader.readmac();	
					
				}else if(args[0].equals("writemac")) {
					System.out.println("starte FlashLoader...");
					
					FlashLoader flashLoader = new FlashLoader();
					flashLoader.writemac();	
					
				}else if(args[0].equals("reset")) {
					System.out.println("starte FlashLoader...");
					
					FlashLoader flashLoader = new FlashLoader();
					flashLoader.reset();	
					
				}
				

				//der Meta-Daten Service
				if(args[0].equals("metadata")) {
					System.out.println("starte Meta-Daten Service...");
					
					String id = cmd.getOptionValue("id");
					String microcontroller = cmd.getOptionValue("microcontroller");
					String sensor = cmd.getOptionValue("sensor");
					String server = cmd.getOptionValue("server");
					
					OverlayClient metaService = new OverlayClient();	
					metaService.setServer(server);
					
					if(id != null){
						metaService.sucheKnotenMitID(id);
					}
					else if(microcontroller != null){
						metaService.sucheKnotenMitMicrocontroller(microcontroller);
					}
					else if(sensor != null){
						metaService.sucheKnotenMitSensor(sensor);
					}
				}
			}	
			
			last_line = buffer.readLine();
			args = last_line.split(" ");
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("Diese Option gibt es nicht.");
			}
		}
		buffer.close();
	}
}
