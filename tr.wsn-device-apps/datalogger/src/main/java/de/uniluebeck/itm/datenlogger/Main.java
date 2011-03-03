package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	//private static Log log = LogFactory.getLog(Main.class);
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

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args.length == 0) {
			printHelp(options);
		} else {
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("One of the parameters is not registered.");
			}
			if (cmd != null) {
				// standard-options
				if (cmd.hasOption("help")) {
					printHelp(options);
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
					PausableWriter writer = initWriter(brackets_filter, regex_filter, location, output);
					
					Datalogger datenlogger = new Datalogger(writer, user,
							password, port, server, device, id);
					datenlogger.connect();
					datenlogger.startlog();

					while (true) {
						while (true) {
							final char in = (char) System.in.read();
							if (in == 10) {
								writer.pause();
								System.out.print("Write-mode entered, please enter your command: ");
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
									if(tokens.length < 2){
										writer = initWriter(writer.getBracketFilter(), writer.getRegexFilter(), null, output);
										datenlogger.setWriter(writer);
									}else {
										writer = initWriter(writer.getBracketFilter(), writer.getRegexFilter(), tokens[1], output);
										datenlogger.setWriter(writer);
									}
								}
								System.out.println("Write-mode leaved!");
								writer.resume();
							}
						}
					}
				}
			}
		}
	}
	
	public static PausableWriter initWriter(String brackets_filter, String regex_filter, String location, String output){
		
		PausableWriter writer = null;
		
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
		
		return writer;
	}
	
	
	public static void printHelp(Options options){
		System.out.println("Example:");
		System.out
				.println("Datalogger: Remote example: startlog -filter (104,23,4)&(104,24,5) -location filename.txt -server localhost -id 1 -port 8181");
		System.out
		.println("Datalogger: Local example: startlog -filter .*(4|3)*. -device telosb -port 1464");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
