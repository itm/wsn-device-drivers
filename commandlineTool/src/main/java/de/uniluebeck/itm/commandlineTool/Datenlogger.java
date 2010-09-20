package de.uniluebeck.itm.commandlineTool;

public class Datenlogger {
	
	String port;
	String server;
	String filters;
	String location;
	boolean gestartet = false;

	public Datenlogger(){
		
	}
	
	private String[] parseFilter(String filter){
		return new String[5];
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public  void getloggers(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
	}	
	
	public  void startlog(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Filter: " + filters);
		System.out.println("Location: " + location);
		gestartet = true;
		System.out.println("\nStarte das Loggen des Knotens....");
	}	
	
	public  void stoplog(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		gestartet = false;
		System.out.println("\nDas Loggen des Knotens wurde beendet.");
	}
	
	public  void addfilter(){
		
	}
}
