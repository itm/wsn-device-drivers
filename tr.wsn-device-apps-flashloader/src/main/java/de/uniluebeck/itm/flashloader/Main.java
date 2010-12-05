package de.uniluebeck.itm.flashloader;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.uniluebeck.itm.devicedriver.MacAddress;

public class Main {
	
	private static double version = 0.1;
	
	public static void main(String[] args) throws IOException {
		// create Options object
		Option help_option = new Option( "help", "print this message" );
		Option version_option = new Option( "version", "print the version information" );
		
		Options options = new Options();
		
		options.addOption(help_option);
		options.addOption(version_option);
		
		// add options for FlashLoader
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("file", true, "Enthält das Programm, das geflasht werden soll");

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
			//standard-options
			if(cmd.hasOption("help")){
				System.out.println("Aufrufbeispiele:");
				System.out.println("Flashloader: flash -port x -file programm.bin");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if(cmd.hasOption("version")){
				System.out.println(version);
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
				flashLoader.flash(file);	
				
			}else if(args[0].equals("readmac")) {
				System.out.println("starte FlashLoader...");
				
				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				flashLoader.readmac();	
				
			}else if(args[0].equals("writemac")) {
				System.out.println("starte FlashLoader...");
				
				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				MacAddress macAdresse = new MacAddress(1024);
				flashLoader.writemac(macAdresse);
				
			}else if(args[0].equals("reset")) {
				System.out.println("starte FlashLoader...");
				
				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				flashLoader.reset();	
				
			}
		}
	}
}
