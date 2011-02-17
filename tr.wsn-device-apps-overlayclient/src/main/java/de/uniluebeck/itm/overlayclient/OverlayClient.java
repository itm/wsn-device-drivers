package de.uniluebeck.itm.overlayclient;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.remote.client.*;
import de.uniluebeck.itm.metadaten.remote.entity.*;

/**
 * The Class OverlayClient.
 */
public class OverlayClient {

	/**
	 * Instantiates a new overlay client.
	 */
	public OverlayClient() {

	}

	/**
	 * Search device with id.
	 * 
	 * @param ID
	 *            the iD
	 */
	public void searchDeviceWithId(String ID) {
		System.out.println("Start Overlaysuche...");
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setId(ID);
		String query = ""; // not used
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Error!");
		}
		System.out.println("Results: " + results.size());
		for (Node node : results) {
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}

	/**
	 * Search device with microcontroller.
	 * 
	 * @param microcontroller
	 *            the microcontroller
	 */
	public void searchDeviceWithMicrocontroller(String microcontroller) {
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setMicrocontroller(microcontroller);
		String query = ""; // not used
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Error!");
		}
		System.out.println("Results: " + results.size());
		for (Node node : results) {
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}

	/**
	 * Search device with the capability
	 * 
	 * @param sensor
	 *            the sensor
	 */
	public void searchDeviceWithCapability(String sensor) {
		List<Capability> sensoren = new ArrayList<Capability>();
		Capability capability = new Capability(sensor, null, null, 0);
		sensoren.add(capability);
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setCapabilityList(sensoren);
		String query = ""; // not used
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Error!");
		}
		System.out.println("Results: " + results.size());
		for (Node node : results) {
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}
}
