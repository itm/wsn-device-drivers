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
		options.addOption("timeout", true, "Timeout while flashing the device");
		
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
				FlashLoader flashLoader = new FlashLoader();
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
					read_cmd(cmd, flashLoader);
					String file = cmd.getOptionValue("file");
					flashLoader.flash(file);

				} else if (args[0].equals("readmac")) {
					read_cmd(cmd, flashLoader);
					flashLoader.readmac();

				} else if (args[0].equals("writemac")) {
				    read_cmd(cmd, flashLoader);
					MacAddress macAdress = new MacAddress(1024);
					flashLoader.writemac(macAdress);

				} else if (args[0].equals("reset")) {
					read_cmd(cmd, flashLoader);
					flashLoader.reset();
				}
			}
		}
	}
	
	public static void read_cmd(CommandLine cmd, FlashLoader flashLoader) throws IOException {
		String port = cmd.getOptionValue("port");
		String server = cmd.getOptionValue("server");
		String user = cmd.getOptionValue("user");
		String password = cmd.getOptionValue("passwd");
		String device = cmd.getOptionValue("device");
		String id = cmd.getOptionValue("id");
		String timeout = cmd.getOptionValue("timeout");

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
		flashLoader.setPort(port);
		flashLoader.setServer(server);
		flashLoader.setUser(user);
		flashLoader.setPassword(password);
		flashLoader.setDevice(device);
		flashLoader.setId(id);
		if(timeout != null){
			flashLoader.setTimeout(timeout);
		}
		flashLoader.connect();
	}
}
