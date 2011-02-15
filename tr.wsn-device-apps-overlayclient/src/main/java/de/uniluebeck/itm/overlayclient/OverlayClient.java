package de.uniluebeck.itm.overlayclient;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.remote.client.*;
import de.uniluebeck.itm.metadaten.remote.entity.*;

/**
 * The Class OverlayClient.
 */
public class OverlayClient {

	private String server;
	
	/**
	 * Instantiates a new overlay client.
	 */
	public OverlayClient(){
		
	}

	/**
	 * Sets the server.
	 *
	 * @param server the new server
	 */
	public void setServer(String server) {
		this.server = server;
	}
	
	/**
	 * Suche knoten mit id.
	 *
	 * @param ID the iD
	 */
	public void sucheKnotenMitID(String ID){
		System.out.println("Starte Overlaysuche...");
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setId(ID);
		String query = "";		//TODO Welche Query?
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Fehler!");
		}
		System.out.println("Anzahl der Ergebnisse: " + results.size());
		for(Node node:results){
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}
	
	/**
	 * Suche knoten mit microcontroller.
	 *
	 * @param microcontroller the microcontroller
	 */
	public void sucheKnotenMitMicrocontroller(String microcontroller){
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setMicrocontroller(microcontroller);
		String query = "";		//TODO Welche Query?
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Fehler!");
		}
		System.out.println("Anzahl der Ergebnisse: " + results.size());
		for(Node node:results){
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}
	
	/**
	 * Suche knoten mit sensor.
	 *
	 * @param sensor the sensor
	 */
	public void sucheKnotenMitSensor(String sensor){
		List<Capability> sensoren = new ArrayList<Capability>();
		Capability capability = new Capability(sensor, null, null, 0);
		sensoren.add(capability);
		MetaDatenClient client = new MetaDatenClient();
		Node queryExample = new Node();
		queryExample.setCapabilityList(sensoren);
		String query = "";		//TODO Welche Query?
		List<Node> results = new ArrayList<Node>();
		try {
			results = client.search(queryExample, query);
		} catch (Exception e) {
			System.out.println("Fehler!");
		}
		System.out.println("Anzahl der Ergebnisse: " + results.size());
		for(Node node:results){
			System.out.println(node.getId());
			System.out.println(node.getPort());
			System.out.println(node.getIpAddress());
			System.out.println();
		}
	}
}
