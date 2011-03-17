package de.uniluebeck.itm.overlayclient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.*;

import de.uniluebeck.itm.metadaten.remote.entity.Capability;

/**
 * The Class Main. Console program for the overlay client.
 */
public class Main {

	/** The version. */
	private static double version = 1.0;

	/** The ip regex, to validate the server-address. */
	private static String ipRegex = "(((\\d{1,3}.){3})(\\d{1,3}))";

	/** The capabilityList regex, to validate the List of capabilities. */
	private static String capListRegex = "[[A-Za-z:]+,[0-9]*,[A-Za-z:]*]+(?:" +
			";[[A-Za-z:]+,[0-9]*,[A-Za-z:]*]+)*";

	/** The valid input gets false, when one of the input-parameters is wrong. */
	private static boolean validInput = true;

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(final String[] args) throws IOException {
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
		options.addOption(
				"capabilities",
				true,
				"capabilities you want to search for in this format: name," +
				"default,datatype;name,default,datatype");
		options.addOption("username", true, "username to connect to the server");
		options.addOption("password", true, "password to connect to the server");
		options.addOption("server", true, "IP-Adress of the server");
		options.addOption("serverPort", true, "Port of the server");
		options.addOption("clientPort", true, "Port of the client");
		options.addOption("searchIP", true,
				"search for a device with the given IP-Address");
		options.addOption("description", true,
				"search for a device with the given description");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args.length == 0) {
			// if there is no input, print help message
			printHelp(options);
		} else {
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

				// parameters for connection
				String server = cmd.getOptionValue("server");
				String serverPort = cmd.getOptionValue("serverPort");
				String clientPort = cmd.getOptionValue("clientPort");
				String username = cmd.getOptionValue("username");
				String password = cmd.getOptionValue("password");

				// parameters for searching
				String id = cmd.getOptionValue("id");
				String microcontroller = cmd.getOptionValue("microcontroller");
				String capabilities = cmd.getOptionValue("capabilities");
				String searchIP = cmd.getOptionValue("searchIP");
				String description = cmd.getOptionValue("description");

				// Begin: validate input-data
				if (server == null) {
					System.out.println("Wrong input: Please enter server!");
					validInput = false;
				}
				if (server != null) {
					if (!server.matches(ipRegex) && !server.equals("localhost")) {
						System.out
								.println("Wrong input: This is no valid server address.");
						validInput = false;
					}
				}
				if (serverPort == null) {
					System.out
							.println("Wrong input: Please enter port of the server!");
					validInput = false;
				} else {
					if (!serverPort.matches("\\d*")) {
						System.out
								.println("Wrong input: This is no valid server-port number.");
						validInput = false;
					}
				}
				if (clientPort != null && !clientPort.matches("\\d*")) {
					System.out
							.println("Wrong input: This is no valid client-port number.");
					validInput = false;
				}
				if (id == null && microcontroller == null
						&& capabilities == null && searchIP == null
						&& description == null) {
					System.out
							.println("Wrong input: Please enter id, microcontroller, " +
									"capabilities, searchIP or description to search " +
									"for at least one of these parameters!");
					validInput = false;
				}
				if (searchIP != null) {
					if (!searchIP.matches(ipRegex)
							&& !searchIP.equals("localhost")) {
						System.out
								.println("Wrong input: Please enter searchIP as " +
										"IP-Address.");
						validInput = false;
					}
				}
				if (id != null && !id.matches("\\d*")) {
					System.out
							.println("Wrong input: Please enter id as integer!");
					validInput = false;
				}
				if (capabilities != null) {
					if (!capabilities.matches(capListRegex)) {
						System.out
								.println("Wrong input: Please enter capability-names " +
										"like name,default,datatype;name,default," +
										"datatype or name,default,datatype;name!");
						validInput = false;
					}
				}
				// End: validate input-data

				if (validInput) {
					// username and password is required to connect to the
					// server
					if (server != null
							&& (username == null && password == null || username == null)) {
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

					// in case of searching for capabilites,
					// the paramteter 'capabilites' holds the capabilites in the
					// format: name,default,datatype;name,default,datatype
					// default and datatype are optional
					List<Capability> capabilityList = null;
					if (capabilities != null) {
						capabilityList = new ArrayList<Capability>();
						String[] singleCapabilities = capabilities.split(";");
						for (int i = 0; i < singleCapabilities.length; i++) {
							String capability = singleCapabilities[i];
							String[] capabilityAttr = capability.split(",");
							Capability capabilityObject = new Capability();
							for (int j = 0; j < capabilityAttr.length; j++) {
								if (j == 0) {
									capabilityObject.setName(capabilityAttr[0]);
								} else if (j == 1) {
									if (capabilityAttr[1].matches("\\d*")) {
										int capabilityDefault = Integer
												.parseInt(capabilityAttr[1]);
										capabilityObject
												.setCapDefault(capabilityDefault);
									} else {
										String capabilityDatatype = capabilityAttr[1];
										capabilityObject
												.setDatatype(capabilityDatatype);
									}
								} else if (j == 2) {
									String capabilityDatatype = capabilityAttr[2];
									capabilityObject
											.setDatatype(capabilityDatatype);
								}
							}
							capabilityList.add(capabilityObject);
						}
					}

					OverlayClient metaService = new OverlayClient(username,
							password, server, serverPort, clientPort);

					metaService.searchDevice(id, microcontroller,
							capabilityList, description, searchIP);
				}
			}
		}
	}

	/**
	 * Prints the help.
	 * 
	 * @param options
	 *            the options, which are registered.
	 */
	public static void printHelp(final Options options) {
		System.out.println("Examples:");
		System.out
				.println("Search by id of the node: -id 123 -server 141.48.65.111 " +
						"-serverPort 8080");
		System.out
				.println("Search by the microcontroller of the node: " +
						"-microcontroller \"TI MSP430\" -server 141.48.65.111 " +
						"-serverPort 8080");
		System.out
				.println("Search by one capability of the node: " +
						"-capabilities urn:wisebed:node:capability:light " +
						"-server 141.48.65.111 " +
						"-serverPort 8080");
		System.out
				.println("Search by three capabilities names: " +
						"-capabilities urn:wisebed:node:capability:light;" +
						"urn:wisebed:node:capability:temp;urn:wisebed:node:" +
						"capability:gas " +
						"-server 141.48.65.111 -serverPort 8080");
		System.out
				.println("Search by three capabilities with name,default," +
						"datatype: -capabilities urn:wisebed:node:capability:light," +
						"6,int;urn:wisebed:node:capability:temp," +
						"7,int;urn:wisebed:node:capability:gas,3,int " +
						"-server 141.48.65.111 -serverPort 8080");
		System.out
				.println("Search by description of the node: -description wisebed" +
						" -server 141.48.65.111 -serverPort 8080");
		System.out
				.println("Search by IP-Address of the node: -searchIP 141.49.65.111" +
						" -server 141.48.65.111 -serverPort 8080");
		System.out
				.println("Search by IP-Address and description of the node: " +
						"-searchIP 141.49.65.111 -description wisebed " +
						"-server 141.48.65.111 -serverPort 8080");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
