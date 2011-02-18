package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {

	private static Log log = LogFactory.getLog(Main.class);
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

		// add options for Datenlogger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("location", true, "path to the output file");
		options.addOption("brackets_filter", true,
				"(datatype,begin,value)-filter");
		options.addOption("regex_filter", true, "regular expression-filter");
		options.addOption("user", true, "username to connect to the server");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("device", true,
				"type of sensornode in local case: jennec, telosb oder pacemate");
		options.addOption("output", true, "Coding of the output data");
		options.addOption("id", true, "ID of the device in remote case");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("One of the parameters is not registered.");
		}
		if (cmd != null) {
			Datalogger datenlogger = new Datalogger();

			// standard-options
			if (cmd.hasOption("help")) {
				System.out.println("Example:");
				System.out
						.println("Datalogger: startlog -filter 0a, 0b, 54 -location filename.txt -server 141.83.1.546 -port 1282");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if (cmd.hasOption("version")) {
				System.out.println(version);
			}

			// der Datenlogger
			if (args[0].equals("startlog")) {
				System.out.println("start Datalogger...");

				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String brackets_filter = cmd.getOptionValue("brackets_filter");
				String regex_filter = cmd.getOptionValue("regex_filter");
				String location = cmd.getOptionValue("location");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String output = cmd.getOptionValue("output");
				String id = cmd.getOptionValue("id");

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
				datenlogger.setUser(user);
				datenlogger.setPassword(password);
				datenlogger.setPort(port);
				datenlogger.setServer(server);
				datenlogger.setKlammer_filter(brackets_filter);
				datenlogger.setRegex_filter(regex_filter);
				datenlogger.setLocation(location);
				datenlogger.setDevice(device);
				datenlogger.setOutput(output);
				datenlogger.setId(id);
				datenlogger.connect();
				datenlogger.startlog();
			}
			while (true) {
				try {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(System.in));
					String input = in.readLine();
					if (input.startsWith("-brackets_filter")) {
						String delims = " ";
						String[] tokens = input.split(delims);
						datenlogger.add_klammer_filter(tokens[1]);
					} else if (input.startsWith("-regex_filter")) {
						String delims = " ";
						String[] tokens = input.split(delims);
						datenlogger.add_regex_filter(tokens[1]);
					} else if (input.equals("stoplog")) {
						datenlogger.stoplog();
						System.exit(0);
					} else if (input.startsWith("-location")) {
						String delims = " ";
						String[] tokens = input.split(delims);
						datenlogger.setLocation(tokens[1]);
					} else if (input.startsWith("e")) {
						datenlogger.stoplog();
						System.exit(0);
					}
				} catch (Exception ex) {
					log.error("Error while reading from terminal.");
				}
			}
		}
	}
}
