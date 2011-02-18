package de.uniluebeck.itm.overlayclient;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.remote.client.*;
import de.uniluebeck.itm.metadaten.remote.entity.*;

/**
 * The Class OverlayClient.
 */
public class OverlayClient {
	
	private String username;
	private String password;
	private String server;
	private String server_port;
	private String client_port;
	
	public void setUsername(String username){
		username = username;
	}
	
	public void setPassword(String password){
		password = password;
	}
	
	public void setServer(String server){
		server = server;
	}
	
	public void setServer_port(String server_port){
		server_port = server_port;
	}
	
	public void setClient_port(String client_port){
		client_port = client_port;
	}
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
	public void searchDeviceWithId(String ID) throws java.lang.Exception {
		System.out.println("Start Overlaysuche...");
		if(client_port != null){
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port), Integer.valueOf(client_port));
		}else{
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port));
		}
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
	public void searchDeviceWithMicrocontroller(String microcontroller) throws java.lang.Exception {
		if(client_port != null){
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port), Integer.valueOf(client_port));
		}else{
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port));
		}
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
	public void searchDeviceWithCapability(String sensor) throws java.lang.Exception {
		List<Capability> sensoren = new ArrayList<Capability>();
		Capability capability = new Capability(sensor, null, null, 0);
		sensoren.add(capability);
		if(client_port != null){
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port), Integer.valueOf(client_port));
		}else{
			MetaDatenClient client = new MetaDatenClient(username, password, server, Integer.valueOf(server_port));
		}
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
