package de.uniluebeck.itm.tcp.server;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * @author Andreas Maier
 * 
 */
public class Main {

	/**
	 * defaultPort is 8080
	 */
	private static final int DEFAULTPORT = 8080;
	/**
	 * defaultHost is localhost
	 */
	private static String defaultHost = "localhost";
	/**
	 * 
	 */
	private static String devicesPath = "";
	/**
	 * 
	 */
	private static String configPath = "";
	/**
	 * 
	 */
	private static String sensorsPath = "";
	/**
	 * 
	 */
	private static boolean metaDaten = false;
	/**
	 * version number of the startScript
	 */
	private static final double VERSION = 0.1;
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(final String[] args) throws IOException {

		final Option helpOption = new Option("help", "print this message");
		final Option versionOption = new Option("version",
				"print the version information");

		final Options options = new Options();

		options.addOption(helpOption);
		options.addOption(versionOption);

		// add options for Server
		options.addOption("p", true, "port of the Server");
		options.addOption("h", true, "host of the Server");
		options.addOption("d", true, "path to the devices.xml file");
		options.addOption("c", true, "path to the config.xml file");
		options.addOption("s", true, "path to the sensors.xml file");
		options.addOption("m", true, "if set the Metadatenservice will be enabled");

		// for help statement
		final HelpFormatter formatter = new HelpFormatter();

		final CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args.length == 0) {
			
			final Server server = new Server(defaultHost, DEFAULTPORT, devicesPath,
					configPath, sensorsPath, false);
			server.start();
			System.out.println("No Parameters found, the Server will start with default-config.");
		} else {
			try {
				cmd = parser.parse(options, args);
			} catch (final ParseException e) {
				System.out.println("One of the parameters is not registered.");
			}
			if (cmd != null) {

				// standard-options
				if (cmd.hasOption("help")) {
					System.out.println("Example:");
					System.out
							.println("Server: -p 8080 -h localhost -d devices.xml -c config.xml -s sensors.xml");
					System.out.println("");
					formatter.printHelp("help", options);
					System.exit(-1);
				}
				if (cmd.hasOption("version")) {
					System.out.println(VERSION);
				}
				System.out.println("start Server...");

				int port = DEFAULTPORT;
				try {
					port = Integer.parseInt(cmd.getOptionValue("p"));
				} catch (final NumberFormatException e) {
					System.out
							.println("No parameter for Port was inserted. The standard-Port "
									+ DEFAULTPORT + " will be used!");
				}
				if(cmd.hasOption("h")){
					defaultHost = cmd.getOptionValue("h");
				} else{
					System.out
					.println("No parameter for Host was inserted. The standard-Host "
							+ defaultHost + " will be used!");
				}
				if(cmd.hasOption("d")){
					devicesPath = cmd.getOptionValue("d");
				}
				if(cmd.hasOption("c")){
					configPath = cmd.getOptionValue("c");
				}
				if(cmd.hasOption("s")){
					sensorsPath = cmd.getOptionValue("s");
				}
				if(cmd.hasOption("m") && cmd.getOptionValue("m").equalsIgnoreCase("1") ){
					metaDaten = true;
				}

				final Server server = new Server(defaultHost, port, devicesPath,
						configPath, sensorsPath, metaDaten);
				server.start();

			}
		}

	}

}
