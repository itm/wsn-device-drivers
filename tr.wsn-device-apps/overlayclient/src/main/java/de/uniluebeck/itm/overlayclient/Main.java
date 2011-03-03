package de.uniluebeck.itm.overlayclient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;

/**
 * The Class Main.
 */
public class Main {

	/** The version. */
	private static double version = 0.1;

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException, java.lang.Exception {
		// create Options object
		Option help_option = new Option("help", "print this message");
		Option version_option = new Option("version",
				"print the version information");

		Options options = new Options();

		options.addOption(help_option);
		options.addOption(version_option);

		// add options for Meta-Service
		options.addOption("id", true, "id to search for");
		options.addOption("microcontroller", true,
				"microcontroller to search for");
		options.addOption("sensor", true, "sensor to search for");
		options.addOption("username", true, "username to connect to the sever");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("server", true, "IP-Adress of the server");
		options.addOption("server_port", true, "Port of the server");
		options.addOption("client_port", true, "Port of the client");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if(args.length == 0){
			printHelp(options);
		}
		else{
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("One of these options is not registered.");
			}
			if (cmd != null) {
				// standard-options
				if (cmd.hasOption("help")) {
					printHelp(options);
				}
				if (cmd.hasOption("version")) {
					System.out.println(version);
				}

				// der Meta-Daten Service
				if (args[0].equals("metadata")) {
					System.out.println("start Meta-Data Service...");

					String id = cmd.getOptionValue("id");
					String microcontroller = cmd.getOptionValue("microcontroller");
					String sensor = cmd.getOptionValue("sensor");
					String user = cmd.getOptionValue("username");
					String password = cmd.getOptionValue("passwd");
					String server = cmd.getOptionValue("server");
					String server_port = cmd.getOptionValue("server_port");
					String client_port = cmd.getOptionValue("client_port");
					
					if (server != null && (user == null && password == null || user == null)) {
						System.out.println("Username and Password is missing.");
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));
						System.out.print("Username: ");
						user = in.readLine();
						System.out.print("Password: ");
						password = in.readLine();
						in.close();
					}
					if (server != null && (password == null)) {
						System.out.println("Password is missing.");
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));
						System.out.print("Password: ");
						password = in.readLine();
						in.close();
					}
					
					OverlayClient metaService = new OverlayClient(user, password, server, server_port, client_port);

					if (id != null) {
						metaService.searchDeviceWithId(id);
					} else if (microcontroller != null) {
						metaService
								.searchDeviceWithMicrocontroller(microcontroller);
					} else if (sensor != null) {
						metaService.searchDeviceWithCapability(sensor);
					}
				}
			}
		}
	}
	
	public static void printHelp(Options options){
		System.out.println("Example:");
		System.out.println("Meta-Data Service: metadata -id 123 -server localhost -server_port 8181");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
