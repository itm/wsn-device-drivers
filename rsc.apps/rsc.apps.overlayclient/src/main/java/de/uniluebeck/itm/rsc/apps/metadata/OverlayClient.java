package de.uniluebeck.itm.rsc.apps.metadata;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.remote.client.MetaDatenClient;
import de.uniluebeck.itm.metadaten.remote.entity.Capability;
import de.uniluebeck.itm.metadaten.remote.entity.Node;

/**
 * The Class OverlayClient. Connects to the MetaDataServer and searches for
 * nodes in the network.
 */
public class OverlayClient {

	private String username;
	private String password;
	private String server;
	private String serverPort;
	private String clientPort;

	/**
	 * Instantiates a new overlay client.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param server
	 *            the server
	 * @param serverPort
	 *            the server port
	 * @param clientPort
	 *            the client port
	 */
	public OverlayClient(final String username, final String password,
			final String server, final String serverPort,
			final String clientPort) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
	}

	/**
	 * Search device with the given id, microcontroller and/or capabilities.
	 * 
	 * @param id
	 * @param microcontroller
	 * @param capabilities
	 * @throws Exception
	 *             exception while searching the nodes
	 */
	public void searchDevice(final String id, final String microcontroller,
			final List<Capability> capabilities, final String description,
			final String searchIP) {
		// connecting to the server
		MetaDatenClient client = null;
		if (clientPort != null) {
			client = new MetaDatenClient(username, password, server,
					Integer.valueOf(serverPort),
					Integer.valueOf(clientPort));

		} else {
			client = new MetaDatenClient(username, password, server,
					Integer.valueOf(serverPort));
		}

		// searching by example
		Node queryExample = new Node();
		if (id != null) {
			// searching device with given id
			queryExample.setId(id);
		}
		if (microcontroller != null) {
			// searching device with given microcontroller
			queryExample.setMicrocontroller(microcontroller);
		}
		if (capabilities != null) {
			// searching device with given capabilites
			queryExample.setCapabilityList(capabilities);
		}
		if (description != null) {
			// searching device with given description
			queryExample.setDescription(description);
		}
		if (searchIP != null) {
			// searching device with given IP-Address
			queryExample.setIpAddress(searchIP);
		}
		String query = ""; // String for searching by query. This is not used
							// here.
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Error while searching the node.");
		}

		// printing the results
		System.out.println("Results: " + results.size());
		for (int i = 0; i < results.size(); i++) {
			System.out.println();
			System.out.println("Result " + (i + 1) + ":");
			printNode(results.get(i));
		}
	}

	/**
	 * Prints the node.
	 * 
	 * @param node
	 *            the node
	 */
	public void printNode(final Node node) {
		System.out
				.println("_______________________________________________________________");
		System.out.println("ID: " + node.getId() + "			Microcontroller: "
				+ node.getMicrocontroller());
		System.out.println("Description: " + node.getDescription()
				+ "		Timestamp: " + node.getTimestamp());
		System.out.println("IP-Address: " + node.getIpAddress());
		List<Capability> capabilities = node.getCapabilityList();
		System.out.println("Capabilites:");
		System.out
				.println("---------------------------------------------------------------");
		for (int i = 0; i < capabilities.size(); i++) {
			System.out.println("ID: " + capabilities.get(i).getId()
					+ "		Name: " + capabilities.get(i).getName());
			System.out.println("Unit: " + capabilities.get(i).getUnit()
					+ "		Datatype: " + capabilities.get(i).getDatatype());
			System.out.println("Default: "
					+ capabilities.get(i).getCapDefault());
		}
		System.out
				.println("_______________________________________________________________");
		System.out.println();
	}
}
