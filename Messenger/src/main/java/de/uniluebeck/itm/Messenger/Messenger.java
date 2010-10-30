package de.uniluebeck.itm.Messenger;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.nulldevice.NullDevice;

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
		
		NullDevice nullDevice = new NullDevice();
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Die Nachricht wurde gesendet.");
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
			nullDevice.createSendOperation().execute(callback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
