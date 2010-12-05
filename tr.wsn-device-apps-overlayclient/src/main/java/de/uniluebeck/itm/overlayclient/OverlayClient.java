package de.uniluebeck.itm.OverlayClient;

public class OverlayClient {

	private String server;
	
	public OverlayClient(){
		
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void sucheKnotenMitID(String ID){
		System.out.println("Der Knoten mit der ID: " +ID+ " wird gesucht...");
	}
	
	public void sucheKnotenMitMicrocontroller(String microcontroller){
		System.out.println("Die Knoten mit dem Microconroller: " +microcontroller+ " werden gesucht...");
	}
	
	public void sucheKnotenMitSensor(String sensor){
		System.out.println("Die Knoten mit dem Sensor: " +sensor+ " werden gesucht...");
	}
}
