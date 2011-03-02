package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

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
		options.addOption("output", true, "Coding of the output data as hex");
		options.addOption("id", true, "ID of the device in remote case");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args.length == 0) {
			formatter.printHelp("help", options);
		} else {
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("One of the parameters is not registered.");
			}
			if (cmd != null) {
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
					String brackets_filter = cmd
							.getOptionValue("brackets_filter");
					String regex_filter = cmd.getOptionValue("regex_filter");
					String location = cmd.getOptionValue("location");
					String user = cmd.getOptionValue("user");
					String password = cmd.getOptionValue("passwd");
					String device = cmd.getOptionValue("device");
					String output = cmd.getOptionValue("output");
					String id = cmd.getOptionValue("id");

					if (server != null
							&& (user == null && password == null || user == null)) {
						System.out.println("Username and Password is missing.");
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));
						System.out.print("Username: ");
						user = in.readLine();
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
					// Init Writer
					PausableWriter writer;
					if (location != null) {
						if (output != null && output.equals("hex")) {
							writer = new HexFileWriter();
							writer.setLocation(location);
						} else if (output != null && output.equals("byte")) {
							writer = new ByteFileWriter();
							writer.setLocation(location);
						} else {
							writer = new StringFileWriter();
							writer.setLocation(location);
						}
					} else {
						if (output != null && output.equals("hex")) {
							writer = new HexConsoleWriter();
						} else {
							writer = new StringConsoleWriter();
						}
					}
					if (regex_filter != null) {
						writer.setRegexFilter(regex_filter);
					}
					if (brackets_filter != null) {
						writer.setBracketFilter(brackets_filter);
					}

					Datalogger datenlogger = new Datalogger(writer, user,
							password, port, server, device, id);
					datenlogger.connect();
					datenlogger.startlog();

					while (true) {
						while (true) {
							final char input = (char) System.in.read();
							writer.pause();
							if (input == 10) {
								break;
							}
						}
						String input = new BufferedReader(
								new InputStreamReader(System.in)).readLine();
						if (input.startsWith("-brackets_filter")) {
							String delims = " ";
							String[] tokens = input.split(delims);
							writer.addBracketFilter(tokens[1]);
						} else if (input.startsWith("-regex_filter")) {
							String delims = " ";
							String[] tokens = input.split(delims);
							writer.addRegexFilter(tokens[1]);
						} else if (input.equals("stoplog")) {
							datenlogger.stoplog();
							System.exit(0);
						} else if (input.startsWith("-location")) {
							String delims = " ";
							String[] tokens = input.split(delims);
							writer.setLocation(tokens[1]);
						}
						writer.resume();
					}
				}
			}
		}
	}
}
