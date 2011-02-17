package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;

public class Main {
	
	private static double version = 0.1;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		// create Options object
		Option help_option = new Option( "help", "print this message" );
		Option version_option = new Option( "version", "print the version information" );
		
		Options options = new Options();
		
		options.addOption(help_option);
		options.addOption(version_option);

		// add options for Datenlogger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("location", true, "Ausgabeziel der Daten, die geloggt werden");
		options.addOption("klammer_filter", true, "Kombination der Filtertypen: (Datentyp,Beginn,Wert)-Filter");
		options.addOption("regex_filter", true, "Kombination der Filtertypen: Regular Expression-Filter");
		options.addOption("user", true, "Benutzername, um sich auf einen Server zu verbinden");
		options.addOption("passwd", true, "Passwort, um sich auf einen Server zu verbinden");
		options.addOption("device", true, "Art des Geraets im lokalen Fall: jennec, telosb oder pacemate");
		options.addOption("output", true, "Art der Ausgabe(Hex=1, String=0)");
		options.addOption("id", true, "ID des Geraets im Remote-Fall");
		
		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Diese Option gibt es nicht.");
		}
		if(cmd != null){
			Datenlogger datenlogger = new Datenlogger();		
			
			//standard-options
			if(cmd.hasOption("help")){
				System.out.println("Aufrufbeispiele:");
				System.out.println("Datenlogger: startlog -filter 0a, 0b, 54 -location filename.txt -port 141.83.1.546:1282");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if(cmd.hasOption("version")){
				System.out.println(version);
			}
			
			//der Datenlogger
		    if(args[0].equals("startlog")) {
				System.out.println("starte Datenlogger...");
				
				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String klammer_filter = cmd.getOptionValue("klammer_filter");
				String regex_filter = cmd.getOptionValue("regex_filter");
				String location = cmd.getOptionValue("location");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String output = cmd.getOptionValue("output");
				String id = cmd.getOptionValue("id");
				
				if(server != null && (user == null || password == null)){
					System.out.println("Bitte geben Sie Benutzername und Passwort ein, um sich zu dem Server zu verbinden.");
				}
				else{
					datenlogger.setPort(port);
					datenlogger.setServer(server);
					datenlogger.setKlammer_filter(klammer_filter);
					datenlogger.setRegex_filter(regex_filter);
					datenlogger.setLocation(location);
					datenlogger.setUser(user);
					datenlogger.setPassword(password);
					datenlogger.setDevice(device);
					datenlogger.setOutput(output);
					datenlogger.setId(id);
					datenlogger.connect();
					datenlogger.startlog();
				}	
			}
			while(true){
	            try {
	                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	                String input = in.readLine();
	                if(input.startsWith("-klammer_filter")){
	                	String delims = " ";
	            		String[] tokens = input.split(delims);
	                	datenlogger.add_klammer_filter(tokens[1]);
	                }else if(input.startsWith("-regex_filter")){
	                	String delims = " ";
	            		String[] tokens = input.split(delims);
	            		datenlogger.add_regex_filter(tokens[1]);
	                }else if(input.equals("stoplog")){
	                	datenlogger.stoplog();
	                	System.exit(0);
	                }
	                else if(input.startsWith("-location")){
	                	String delims = " ";
	            		String[] tokens = input.split(delims);
	            		datenlogger.setLocation(tokens[1]);
	                }
	                else if(input.startsWith("e")){
	                	datenlogger.stoplog();
	                	System.exit(0);
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }          
	        }
		}
	}
}
