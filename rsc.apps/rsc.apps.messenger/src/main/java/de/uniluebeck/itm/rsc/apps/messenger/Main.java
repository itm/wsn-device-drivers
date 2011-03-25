package de.uniluebeck.itm.rsc.apps.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;

/**
 * The Class Main.
 */
public class Main {
	
	/** The version */
	private static String version = "1.0";

	/** The ip regex, to validate the server-address. */
	private static String ipRegex = "(((\\d{1,3}.){3})(\\d{1,3}))";

	/** The hex regex., to validate the message as hex. */
	private static String hexRegex = "\\A\\b[0-9a-fA-F]+\\b\\Z";

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

		// add options for Messenger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("message", true, "message to send as hex-code");
		options.addOption("username", true, "username to connect to the server");
		options.addOption("password", true, "password to connect to the server");
		options.addOption("device", true,
				"type of the device in local case: jennic, telosb or pacemate");
		options.addOption("id", true, "ID of the device in remote case");
		options.addOption("messageType", true, "Type of the Message to be send");
		options.addOption("timeout", true, "optional timeout for the operation");

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
				} else if (cmd.hasOption("version")) {
					System.out.println(version);
				} else {

					// parameters for connection
					String server = cmd.getOptionValue("server");
					String port = cmd.getOptionValue("port");
					String id = cmd.getOptionValue("id");
					String username = cmd.getOptionValue("username");
					String password = cmd.getOptionValue("password");
					String device = cmd.getOptionValue("device");

					// parameters for the message
					String message = cmd.getOptionValue("message");
					String messageType = cmd.getOptionValue("messageType");

					// parameter to set the timeout of the operation
					String timeout = cmd.getOptionValue("timeout");

					// Begin: validate input-data
					if (device == null && server == null) {
						System.out
								.println("Wrong input: Please enter device(local) "
										+ "or server(remote)!");
						validInput = false;
					}
					if (device != null) {
						if (!device.equals("mock") && !device.equals("jennic")
								&& !device.equals("pacemate")
								&& !device.equals("telosb")) {
							System.out
									.println("Wrong input: The device parameter can "
											+ "only be 'jennic', 'pacemate', " +
													"'telosb' or 'mock'.");
							validInput = false;
						}
					}
					if (server != null) {
						if (!server.matches(ipRegex)
								&& !server.equals("localhost")) {
							System.out
									.println("Wrong input: This is no valid server address.");
							validInput = false;
						}
					}
					if (port == null) {
						System.out.println("Wrong input: Please enter port!");
						validInput = false;
					} else {
						if (!port.matches("\\d*") && !port.matches("COM\\d+")) {
							System.out
									.println("Wrong input: This is no valid port number.");
							validInput = false;
						}
					}
					if (server != null && id == null) {
						System.out
								.println("Wrong input: Please enter id of the device!");
						validInput = false;
					}
					if (id != null && !id.matches("\\d*")) {
						System.out
								.println("Wrong input: Please enter id as integer!");
						validInput = false;
					}
					if (messageType == null) {
						System.out
								.println("Wrong input: Please enter a messageType!");
						validInput = false;
					} else if (!messageType.matches("\\d*")) {
						System.out
								.println("Wrong input: Please enter message-type as integer!");
						validInput = false;
					}
					if (message == null) {
						System.out
								.println("Wrong input: Please enter message!");
						validInput = false;
					} else {
						if (!message.matches(hexRegex)) {
							System.out
									.println("Wrong input: Please enter message as hex!");
							validInput = false;
						}
					}
					if (timeout != null) {
						if (!timeout.matches("\\d*")) {
							System.out
									.println("Wrong input: Please enter timeout as integer!");
							validInput = false;
						}
					}
					// End: validate input-data

					if (validInput) {
						// username and password is required to connect to the
						// server
						if (server != null
								&& (username == null && password == null || username == null)) {
							System.out
									.println("Username and Password is missing.");
							BufferedReader in = new BufferedReader(
									new InputStreamReader(System.in));
							System.out.print("Username: ");
							username = in.readLine();
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

						Messenger messenger = new Messenger(port, server,
								username, password, device, id,
								Integer.valueOf(messageType), timeout);
						messenger.connect();
						messenger.send(message);
					}
				}
			}
		}
	}

	/**
	 * Prints the help.
	 * 
	 * @param options
	 *            the options
	 */
	public static void printHelp(final Options options) {
		System.out.println("Examples:");
		System.out.println("Messenger: Remote example: -message 68616c6c6f "
				+ "-port 8181 -server localhost -id 1 -messageType 1 "
				+ "-username name -password password");
		System.out.println("Messenger: Local example: -message 68616c6c6f "
				+ "-port COM1 -device jennic -messageType 1");
		System.out.println("");
		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
