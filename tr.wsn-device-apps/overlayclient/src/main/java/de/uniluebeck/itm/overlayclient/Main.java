package de.uniluebeck.itm.overlayclient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.*;

import de.uniluebeck.itm.metadaten.remote.entity.Capability;

/**
 * The Class Main.
 * Console program for the overlay client.
 */
public class Main {

	/** The version. */
	private static double version = 1.0;
	
	/** The ip regex, to validate the server-address */
	private static String ipRegex = "(((\\d{1,3}.){3})(\\d{1,3}))";
	
	/** The valid input gets false, when one of the input-parameters is wrong. */
	private static boolean validInput = true;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		// create Options object
		Option helpOption = new Option("help", "print this message");
		Option versionOption = new Option("version",
				"print the version information");

		Options options = new Options();

		options.addOption(helpOption);
		options.addOption(versionOption);

		// add options for Meta-Service
		options.addOption("id", true, "id to search for");
		options.addOption("microcontroller", true,
				"microcontroller to search for");
		options.addOption("capabilities", true, "number of capabilities you want to search for");
		options.addOption("username", true, "username to connect to the server");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("server", true, "IP-Adress of the server");
		options.addOption("serverPort", true, "Port of the server");
		options.addOption("clientPort", true, "Port of the client");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if(args.length == 0){
			//if there is no input, print help message
			printHelp(options);
		}
		else{
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("One of these options is not registered.");
				printHelp(options);
			}
			if (cmd != null) {
				// standard-options
				if (cmd.hasOption("help")) {
					printHelp(options);
				}
				if (cmd.hasOption("version")) {
					System.out.println(version);
				}

				//parameters for connection
				String server = cmd.getOptionValue("server");
				String serverPort = cmd.getOptionValue("serverPort");
				String clientPort = cmd.getOptionValue("clientPort");
				String username = cmd.getOptionValue("username");
				String password = cmd.getOptionValue("passwd");
				
				//parameters for searching
				String id = cmd.getOptionValue("id");
				String microcontroller = cmd.getOptionValue("microcontroller");
				String capabilities = cmd.getOptionValue("capabilities");
				
				//Begin: validate input-data
				if (server == null) {
					System.out.println("Wrong input: Please enter server!");
					validInput = false;
				}
				if (server != null) {
					if (!server.matches(ipRegex) && !server.equals("localhost")) {
						System.out
								.println("Wrong input: This is no valide server address.");
						validInput = false;
					}
				}
				if (serverPort == null) {
					System.out.println("Wrong input: Please enter port of the server!");
					validInput = false;
				}
				if (id == null && microcontroller == null && capabilities == null) {
					System.out.println("Wrong input: Please enter id, microcontroller or capabilities to search for at least one of these parameters!");
					validInput = false;
				}
			    //End: validate input-data
				
				if(validInput){
					//username and password is required to connect to the server
					if (server != null && (username == null && password == null || username == null)) {
						System.out.println("Username and Password is missing.");
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));
						System.out.print("Username: ");
						username = in.readLine();
						System.out.print("Password: ");
						password = in.readLine();
					}
					if (server != null && (password == null)) {
						System.out.println("Password is missing.");
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));
						System.out.print("Password: ");
						password = in.readLine();
					}
					
					//in case of searching for capabilites,
					//the paramteter 'capabilites' holds the number of capabilites,
					//which names have to be entered now:
					List<Capability> capabilityList = null;
					if(capabilities != null){
						int numberOfCapabilities = Integer.parseInt(capabilities);
						capabilityList = new ArrayList<Capability>();
						for(int i = 1; i <= numberOfCapabilities; i++){
							System.out.println("Please enter the name of the "+i+". capability:");
							String capabilityName = new BufferedReader(
									new InputStreamReader(System.in))
									.readLine();
							Capability capability = new Capability(capabilityName, null, null, 0);
							capabilityList.add(capability);
						}
					}
					
					OverlayClient metaService = new OverlayClient(username, password, server, serverPort, clientPort);

					metaService.searchDevice(id, microcontroller, capabilityList);
				}
			}
		}
	}

	/**
	 * Prints the help.
	 *
	 * @param options the options, which are registered.
	 */
	public static void printHelp(Options options){
		System.out.println("Examples:");
		System.out.println("Search by id of the node: -id 123 -server localhost -serverPort 8181");
		//TODO Example for microcontroller
		System.out.println("Search by three capabilites of the node: -capabilites 3 -server localhost -serverPort 8181");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
