package de.uniluebeck.itm.flashloader;

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

import de.uniluebeck.itm.devicedriver.MacAddress;

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

		// add options for FlashLoader
		options.addOption("port", true, "port");
		options.addOption("server", true, "server");
		options.addOption("file", true, "File to flash the device.");
		options.addOption("user", true, "username to connect to the server");
		options.addOption("passwd", true, "password to connect to the server");
		options.addOption("device", true,
				"type of device in local case: jennec, telosb oder pacemate");
		options.addOption("id", true, "ID of the device in remote case");

		// for help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
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
						.println("Flashloader: flash -port x -file programm.bin");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if (cmd.hasOption("version")) {
				System.out.println(version);
			}

			// der FlashLoader
			if (args[0].equals("flash")) {
				System.out.println("start FlashLoader...");

				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String file = cmd.getOptionValue("file");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String id = cmd.getOptionValue("id");

				if (server != null && (user == null || password == null)) {
					System.out.println("Username and Password is missing.");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(System.in));
					System.out.print("Username: ");
					user = in.readLine();
					System.out.print("Password: ");
					password = in.readLine();
					in.close();
				}
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				flashLoader.setUser(user);
				flashLoader.setPassword(password);
				flashLoader.setDevice(device);
				flashLoader.setId(id);
				flashLoader.connect();
				flashLoader.flash(file);

			} else if (args[0].equals("readmac")) {
				System.out.println("start FlashLoader...");

				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String id = cmd.getOptionValue("id");

				if (server != null && (user == null || password == null)) {
					System.out.println("Username and Password is missing.");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(System.in));
					System.out.print("Username: ");
					user = in.readLine();
					System.out.print("Password: ");
					password = in.readLine();
					in.close();
				}
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				flashLoader.setUser(user);
				flashLoader.setPassword(password);
				flashLoader.setDevice(device);
				flashLoader.setId(id);
				flashLoader.connect();
				flashLoader.readmac();

			} else if (args[0].equals("writemac")) {
				System.out.println("start FlashLoader...");

				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String id = cmd.getOptionValue("id");

				if (server != null && (user == null || password == null)) {
					System.out.println("Username and Password is missing.");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(System.in));
					System.out.print("Username: ");
					user = in.readLine();
					System.out.print("Password: ");
					password = in.readLine();
					in.close();
				}
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				MacAddress macAdresse = new MacAddress(1024);
				flashLoader.setUser(user);
				flashLoader.setPassword(password);
				flashLoader.setDevice(device);
				flashLoader.setId(id);
				flashLoader.connect();
				flashLoader.writemac(macAdresse);

			} else if (args[0].equals("reset")) {
				System.out.println("start FlashLoader...");

				String port = cmd.getOptionValue("port");
				String server = cmd.getOptionValue("server");
				String user = cmd.getOptionValue("user");
				String password = cmd.getOptionValue("passwd");
				String device = cmd.getOptionValue("device");
				String id = cmd.getOptionValue("id");

				if (server != null && (user == null || password == null)) {
					System.out.println("Username and Password is missing.");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(System.in));
					System.out.print("Username: ");
					user = in.readLine();
					System.out.print("Password: ");
					password = in.readLine();
					in.close();
				}
				FlashLoader flashLoader = new FlashLoader();
				flashLoader.setPort(port);
				flashLoader.setServer(server);
				flashLoader.setUser(user);
				flashLoader.setPassword(password);
				flashLoader.setDevice(device);
				flashLoader.setId(id);
				flashLoader.connect();
				flashLoader.reset();
			}
		}
	}
}
