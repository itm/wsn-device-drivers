package de.uniluebeck.itm.messenger;

import java.io.BufferedReader;
import java.io.IOException;
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
	public static void main(String[] args) throws IOException {
		// create Options object
		Option help_option = new Option("help", "print this message");
		Option version_option = new Option("version",
				"print the version information");

		Options options = new Options();

		options.addOption(help_option);
		options.addOption(version_option);

		// add options for Messenger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("message", true, "messge to send");
		options.addOption("user", true, "username to connect to the server");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("device", true,
				"type of the device in local case: jennec, telosb oder pacemate");
		options.addOption("id", true, "ID of the device in remote case");
		options.addOption("message_type", true, "Type of the Message to be send");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if(args.length == 0){
			formatter.printHelp("help", options);
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
					System.out.println("Example:");
					System.out
							.println("Messenger: send -message 0a 3f 41 -server 141.83.1.546 -port 1282");
					System.out.println("");
					formatter.printHelp("help", options);
				}
				if (cmd.hasOption("version")) {
					System.out.println(version);
				}

				// der Messenger
				if (args[0].equals("send")) {
					System.out.println("start Messenger...");

					String port = cmd.getOptionValue("port");
					String server = cmd.getOptionValue("server");
					String message = cmd.getOptionValue("message");
					String user = cmd.getOptionValue("user");
					String password = cmd.getOptionValue("passwd");
					String device = cmd.getOptionValue("device");
					String id = cmd.getOptionValue("id");
					String message_type = cmd.getOptionValue("message_type");

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
					Messenger messenger = new Messenger();
					messenger.setPort(port);
					messenger.setServer(server);
					messenger.setUser(user);
					messenger.setPassword(password);
					messenger.setDevice(device);
					messenger.setId(id);
					messenger.setMessage_type(message_type);
					messenger.connect();
					messenger.send(message);
				}
			}
		}
	}
}
