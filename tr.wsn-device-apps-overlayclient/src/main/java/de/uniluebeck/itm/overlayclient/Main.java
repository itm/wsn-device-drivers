package de.uniluebeck.itm.overlayclient;

import java.io.IOException;

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

		// add options for Meta-Service
		options.addOption("id", true, "id to search for");
		options.addOption("microcontroller", true,
				"microcontroller to search for");
		options.addOption("sensor", true, "sensor to search for");

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
				System.out.println("Meta-Data Service: metadata -id 123");
				System.out.println("");
				formatter.printHelp("help", options);
			}
			if (cmd.hasOption("version")) {
				System.out.println(version);
			}

			// der Meta-Daten Service
			if (args[0].equals("metadata")) {
				System.out.println("start Meta-Data Service...");

				String id = cmd.getOptionValue("id");
				String microcontroller = cmd.getOptionValue("microcontroller");
				String sensor = cmd.getOptionValue("sensor");

				OverlayClient metaService = new OverlayClient();

				if (id != null) {
					metaService.searchDeviceWithId(id);
				} else if (microcontroller != null) {
					metaService
							.searchDeviceWithMicrocontroller(microcontroller);
				} else if (sensor != null) {
					metaService.searchDeviceWithCapability(sensor);
				}
			}
		}
	}
}
