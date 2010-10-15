package de.uniluebeck.itm.FlashLoader;

public class FlashLoader {
	
	String port;
	String server;
	String file;
	
	public FlashLoader(){
		
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public void flash(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("File: " + file);
		for(int i=10; i>0; i--){
			System.out.println("Der Knoten wird programmiert: noch "+i+" Sekunden.");
			try{
				Thread.sleep(1000);
			}
			catch(InterruptedException e){
				System.out.println("Sleep Interrupted");
			}
		}
		System.out.println("\nDer Knoten wurde erfolgreich programmiert.");
	}
	
	public void readmac(){

	}
	
	public void writemac(){
		
	}
	
	public void reset(){
		
	}
}
