package de.uniluebeck.itm.rsc.apps.datalogger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.uniluebeck.itm.rsc.remote.client.RemoteConnection;

/**
 * The Class Main. Console program for the datalogger.
 */
public class Main {

	/** The brackets regex, to validate the brackets-filter. */
	private static String bracketsRegex = "(([\\(]+[0-9]+,[0-9]+,[0-9a-zA-Z]+[\\)]+)+(?:"
			+ "[\\|\\&]([\\(]+[0-9]+,[0-9]+,[0-9a-zA-Z]+[\\)]+)+)*)";

	/** The ip regex, to validate the server-address. */
	private static String ipRegex = "(((\\d{1,3}.){3})(\\d{1,3}))";

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
		// create standard-options, with the command and the description
		Option helpOption = new Option("help", "print this message");
		Option versionOption = new Option("version",
				"print the version information");

		// create Options object, which holds the available parameters
		Options options = new Options();

		options.addOption(helpOption);
		options.addOption(versionOption);

		// add options for Datalogger
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("location", true, "path to the output file");
		options.addOption("bracketsFilter", true,
				"(datatype,begin,value)-filter");
		options.addOption("regexFilter", true, "regular expression-filter");
		options.addOption("username", true, "username to connect to the server");
		options.addOption("password", true, "password to connect to the server");
		options.addOption("device", true,
				"type of sensornode in local case: jennic, telosb oder pacemate");
		options.addOption("output", true,
				"Coding alternative of the output data hex or byte");
		options.addOption("id", true, "ID of the device in remote case");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args.length == 0) {
			// if there is no input, print help message
			printHelp(options);
		} else {
			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println("One of the parameters is not registered.");
				printHelp(options);
			}
			if (cmd != null) {
				// standard-options
				if (cmd.hasOption("help")) {
					printHelp(options);
				} else if (cmd.hasOption("version")) {
					System.out.println(getVersion());
				} else {

					// parameters for connection
					String server = cmd.getOptionValue("server");
					String port = cmd.getOptionValue("port");
					String id = cmd.getOptionValue("id");
					String user = cmd.getOptionValue("username");
					String password = cmd.getOptionValue("password");
					String device = cmd.getOptionValue("device");
					// parameters for filtering
					String bracketsFilter = cmd
							.getOptionValue("bracketsFilter");
					String regexFilter = cmd.getOptionValue("regexFilter");
					// parameters for the output of the logging
					String location = cmd.getOptionValue("location");
					String output = cmd.getOptionValue("output");

					// Begin: validate input-data
					if (device == null && server == null) {
						System.out
								.println("Wrong input: Please enter device or server!");
						validInput = false;
					}
					if (device != null) {
						if (!device.equals("mock") && !device.equals("jennic")
								&& !device.equals("pacemate")
								&& !device.equals("telosb")) {
							System.out
									.println("Wrong input: The device parameter can only "
											+ "be 'jennic', 'pacemate', 'telosb' or 'mock'.");
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
					if (bracketsFilter != null) {
						if (!bracketsFilter.matches(bracketsRegex) || 
								!validateBrackets(bracketsFilter)) {
							System.out
									.println("Wrong input: This is no valid " +
											"bracket filter.\nPlease use the format: " +
											"((datatype,begin,value)|(datatype,begin,value))" +
											"&(datatype,begin,value)");
							validInput = false;
						}
					}
					if (output != null) {
						if (!output.equals("hex") && !output.equals("byte")) {
							System.out
									.println("Wrong input: The output parameter can only "
											+ "be 'hex' or 'byte'.");
							validInput = false;
						}
					}
					// End: validate input-data

					if (validInput) {
						// username and password is required to connect to the
						// server
						if (server != null
								&& (user == null && password == null || user == null)) {
							System.out
									.println("Username and Password is missing.");
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
						PausableWriter writer = initWriter(bracketsFilter,
								regexFilter, location, output);

						Datalogger datalogger = new Datalogger(writer, user,
								password, port, server, device, id);
						datalogger.connect();
						try {
							datalogger.startlog();
							writeMode(writer, datalogger, output);
						} finally {
							// close remote connection
							RemoteConnection connection = datalogger
									.getConnection();
							if (connection != null) {
								connection.shutdown(false);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Inits the writer.
	 * 
	 * @param bracketsFilter
	 * @param regexFilter
	 * @param location
	 * @param output
	 * @return the pausable writer
	 */
	public static PausableWriter initWriter(final String bracketsFilter,
			final String regexFilter, final String location, final String output) {

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
		if (regexFilter != null) {
			writer.setRegexFilter(regexFilter);
		}
		if (bracketsFilter != null) {
			writer.setBracketFilter(bracketsFilter);
		}

		return writer;
	}

	/**
	 * Prints the help.
	 * 
	 * @param options
	 *            the options
	 */
	public static void printHelp(final Options options) {
		System.out.println("Examples:");
		System.out.println("Remote example: -bracketsFilter "
				+ "((104,23,1)|(104,23,2))&(104,0,M) "
				+ "-location filename.txt -server localhost -id 1 "
				+ "-port 8181 -username name -password password");
		System.out.println("Local example: -regexFilter .*(4|3).* "
				+ "-device telosb -port COM1");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}

	/**
	 * provides the write-mode to change the location, 
	 * add a filter or stop logging.
	 * 
	 * @param writer
	 *            output-writer
	 * @param datalogger
	 *            the current datalogger
	 * @param datalogger
	 *            the current output-parameter
	 *            
	 */
	private static void writeMode(PausableWriter writer, Datalogger datalogger,
			String output) throws IOException {
		// write-mote, to change parameters while logging.
		while (true) {
			while (true) {
				final char in = (char) System.in.read();
				if (in == 10) { // press 'enter' to enter
								// write-mode
					writer.pause(); // writer get paused, to
									// read the command from the
									// console
					System.out.println("Write-mode entered");
					System.out.println("Options:");
					System.out.println("Enter 'bracketsFilter filter' "
							+ "to add 'filter' to the current "
							+ "brackets-filter.");
					System.out.println("Enter 'regexFilter filter' "
							+ "to add 'filter' to the current "
							+ "regex-filter.");
					System.out.println("Enter 'location location' to "
							+ "change the current location. "
							+ "'location' will set the location "
							+ "to terminal.");
					System.out
							.println("Enter 'stoplog' to exit the program.\n");
					System.out.print("Please enter your command: ");
					String input = new BufferedReader(new InputStreamReader(
							System.in)).readLine();
					if (input.startsWith("bracketsFilter")) {
						// add brackets-filter
						String delims = " ";
						String[] tokens = input.split(delims);
						if (tokens[1].matches("[|&]?" + bracketsRegex) && 
								validateBrackets(tokens[1])) {
							writer.addBracketFilter(tokens[1]);
						} else {
							System.out.println("This is no valid bracket "
									+ "filter.");
						}
					} else if (input.startsWith("regexFilter")) {
						// add regex-filter
						String delims = " ";
						String[] tokens = input.split(delims);
						writer.addRegexFilter(tokens[1]);
					} else if (input.equals("stoplog")) {
						// stop logging
						datalogger.stoplog();
						System.exit(1);
					} else if (input.startsWith("location")) {
						// change the output location
						String delims = " ";
						String[] tokens = input.split(delims);
						if (tokens.length < 2) {
							writer = initWriter(writer.getBracketFilter(),
									writer.getRegexFilter(), null, output);
							datalogger.setWriter(writer);
						} else {
							writer = initWriter(writer.getBracketFilter(),
									writer.getRegexFilter(), tokens[1], output);
							datalogger.setWriter(writer);
						}
					} else {
						System.out.println("Wrong command!");
					}
					System.out.println("Write-mode leaved!");
					writer.resume();
				}
			}
		}
	}


	/**
	 * Reads the version from the pom.properties.
	 */
	public static String getVersion() {
		String versionNumber = "";
		try {
			FileInputStream fstream = new FileInputStream("target/"
					+ "maven-archiver/pom.properties");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			// skip "#Generated by Maven"
			reader.readLine();

			// find "version=..."
			String line;
			while (null != (line = reader.readLine())) {
				String[] parts = line.split("[ \t]*=[ \t]*");
				if ("version".equals(parts[0])) {
					versionNumber = parts[1];
					break;
				}
			}

			reader.close();

		} catch (Exception e) {
			System.out.println("Cannot load the pom.properties.");
		}
		return versionNumber;
	}

	/**
	 * validates the number of opening and closing brackets.
	 * 
	 * @param bracketsFilter
	 * @return true, if there are the same number of 
	 * opening and closing brackets
	 */
	private static boolean validateBrackets(String bracketsFilter){
		int opening = 0;
		int closing = 0;
		for(int i = 0; i < bracketsFilter.length(); i++){
			if(bracketsFilter.charAt(i) == '('){
				opening++;
			}
			if(bracketsFilter.charAt(i) == ')'){
				closing++;
			}
			if(closing>opening)
			{
				break;
			}
		}		
		return opening == closing;
	}
}
