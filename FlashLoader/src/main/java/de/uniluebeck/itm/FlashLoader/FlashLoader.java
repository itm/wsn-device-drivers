package de.uniluebeck.itm.FlashLoader;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.nulldevice.NullDevice;

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
		
		NullDevice nullDevice = new NullDevice();
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Das Programmieren war erfolgreich.");
			}
			public void onCancel() {
				System.out.println("Die Operation wurde abgebrochen");
			}
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
			public void onProgressChange(float fraction) {
				System.out.println("Es tut sich was.");
			}
		};
	
		try {
			nullDevice.createProgramOperation().execute(callback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void readmac(){

	}
	
	public void writemac(){
		
	}
	
	public void reset(){
		
	}
}
