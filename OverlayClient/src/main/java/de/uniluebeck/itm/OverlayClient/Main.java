package de.uniluebeck.itm.OverlayClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;

public class Main {
	
	private static double version = 0.1;
	
	public static void main(String[] args) throws IOException {
		// create Options object
		Option help_option = new Option( "help", "print this message" );
		Option version_option = new Option( "version", "print the version information" );
		
		Options options = new Options();
		
		options.addOption(help_option);
		options.addOption(version_option);

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
		if(cmd != null){
			//standard-options
			if(cmd.hasOption("help")){
				System.out.println("Aufrufbeispiele:");
				System.out.println("Meta-Daten Service: metadata -id 123");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if(cmd.hasOption("version")){
				System.out.println(version);
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
		buffer.close();
	}
}
