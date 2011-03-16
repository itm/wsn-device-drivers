package de.uniluebeck.itm.flashloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.uniluebeck.itm.devicedriver.MacAddress;

/**
 * The Class Main.
 */
public class Main {

	/** The version. */
	private static double version = 1.0;

	/** The ip regex, to validate the server-address. */
	private static String ipRegex = "(((\\d{1,3}.){3})(\\d{1,3}))";

	/** The hex regex, to validate the mac-address. */
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

		// add options for FlashLoader
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("file", true, "file to flash the device.");
		options.addOption("user", true, "username to connect to the server");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("device", true,
				"type of device in local case: jennic, telosb oder pacemate");
		options.addOption("id", true, "ID of the device in remote case");
		options.addOption("timeout", true,
				"optional timeout while flashing the device");
		options.addOption("macAddress", true,
				"the mac-address, that should be written on the device.");

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

				// read and validate the standard options
				FlashLoader flashLoader = readCmd(cmd);

				// flash the device with a given file
				if (args[0].equals("flash")) {
					String file = cmd.getOptionValue("file");
					if (file == null) {
						System.out
								.println("Wrong input: Please enter file to flash the device!");
						validInput = false;
					} else {
						File f = new File(file);
						if (!f.exists()) {
							System.out
									.println("Wrong input: File does not exists!");
							validInput = false;
						}
					}
					if (validInput) {
						flashLoader.connect();
						flashLoader.flash(file);
					}

				}
				// read the mac-address of the device
				else if (args[0].equals("readmac")) {
					if (validInput) {
						flashLoader.connect();
						flashLoader.readmac();
					}

				}
				// write the mac-address of the device with the given address
				else if (args[0].equals("writemac")) {
					String macAddress = cmd.getOptionValue("macAddress");
					if (macAddress == null) {
						System.out
								.println("Wrong input: Please enter macAddress!");
						validInput = false;
					} else {
						if (!macAddress.matches(hexRegex)) {
							System.out
									.println("Wrong input: Please enter macAddress as hex!");
							validInput = false;
						}
					}
					if (validInput) {
						int length = macAddress.length();
						if (length != 16) { // if the length is not 16, fill it
											// with zeros
							for (int i = length; i < 16; i++) {
								macAddress = macAddress + "0";
								length++;
							}
						}
						MacAddress macAdress = new MacAddress(
								hexStringToByteArray(macAddress));
						flashLoader.connect();
						flashLoader.writemac(macAdress);
					}

				}
				// reset the device
				else if (args[0].equals("reset")) {
					if (validInput) {
						flashLoader.connect();
						flashLoader.reset();
					}
				} else {
					System.out
							.println("Wrong input: Please enter program-mode flash, readmac, writemac or reset.\n");
					printHelp(options);
				}
			}
		}
	}

	/**
	 * Parses the Parameters from the given CommandLine, validates them and
	 * gives them to the flashloader-object.
	 * 
	 * @param cmd
	 *            the cmd
	 * @return the flash loader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static FlashLoader readCmd(final CommandLine cmd) throws IOException {
		FlashLoader flashLoader = null;
		// parameters for connection
		String server = cmd.getOptionValue("server");
		String port = cmd.getOptionValue("port");
		String id = cmd.getOptionValue("id");
		String user = cmd.getOptionValue("user");
		String password = cmd.getOptionValue("passwd");
		String device = cmd.getOptionValue("device");
		// parameter to set the timeout of the operation
		String timeout = cmd.getOptionValue("timeout");

		// Begin: validate input-data
		if (device == null && server == null) {
			System.out
					.println("Wrong input: Please enter device(local) or server(remote)!");
			validInput = false;
		}
		if (device != null) {
			if (!device.equals("mock") && !device.equals("jennic")
					&& !device.equals("pacemate") && !device.equals("telosb")) {
				System.out
						.println("Wrong input: The device parameter can only be 'jennic', 'pacemate', 'telosb' or 'mock'.");
				validInput = false;
			}
		}
		if (server != null) {
			if (!server.matches(ipRegex) && !server.equals("localhost")) {
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
			System.out.println("Wrong input: Please enter id of the device!");
			validInput = false;
		}
		if (id != null && !id.matches("\\d*")) {
			System.out.println("Wrong input: Please enter id as integer!");
			validInput = false;
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
			// username and password is required to connect to the server
			if (server != null
					&& (user == null && password == null || user == null)) {
				System.out.println("Username and Password is missing.");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("Username: ");
				user = in.readLine();
				System.out.print("Password: ");
				password = in.readLine();
				in.close();
			}
			if (server != null && (password == null)) {
				System.out.println("Password is missing.");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("Password: ");
				password = in.readLine();
				in.close();
			}

			flashLoader = new FlashLoader(port, server, user, password, device,
					id, timeout);
		}
		return flashLoader;
	}

	/**
	 * Converts a hex-String to a byte array to send the mac-address
	 * @param s
	 *            , hex-String
	 * @return data, the byte array
	 */
	public static byte[] hexStringToByteArray(final String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Prints the help.
	 * @param options
	 *            the options
	 */
	public static void printHelp(final Options options) {
		System.out.println("Examples:");
		System.out
				.println("Flash: Remote-Example: flash -port 8181 -server localhost -id 1 -file jennic.bin");
		System.out
				.println("Flash: Local-Example: flash -port COM1 -file jennec.bin -device jennic");
		System.out
				.println("Write Mac: Local-Example: writemac -port COM1 -device jennic -macAddress 080020aefd7e");
		System.out
				.println("Read Mac: Local-Example: readmac -port COM1 -device jennic");
		System.out
				.println("Reset: Local-Example: reset -port COM1 -device telosb");
		System.out.println("");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
