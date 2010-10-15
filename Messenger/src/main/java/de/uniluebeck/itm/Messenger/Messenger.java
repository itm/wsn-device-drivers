package de.uniluebeck.itm.Messenger;

public class Messenger {
	
	String port;
	String server;
	
	public Messenger(){

	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void send(String message){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Message: " + message);
		System.out.println("\nDie Nachricht wurde erfolgreich gesendet.");
	}
}
