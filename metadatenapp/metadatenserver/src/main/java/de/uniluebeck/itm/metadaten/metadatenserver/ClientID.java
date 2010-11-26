package de.uniluebeck.itm.metadaten.metadatenserver;

import java.util.HashMap;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;


public class ClientID {

	String Message = "init";
	private static HashMap<String,OperationHandle<Void>> handleList = new HashMap<String,OperationHandle<Void>>();
	
	public ClientID(){
		
	}
	
	public void setMessage(String message){
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		Message = message;
		
		
//		try {
//			Thread.sleep(10000000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public String getMessage() {
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Message;
	}

	// Eintragen des OperationHandle mit dem OperationKey in eine HashMap
	public void setHandleList(String OperationKey, OperationHandle<Void> handle) {
		handleList.put(OperationKey, handle);
	}

	// Rueckgabe des richtigen OperationHandle
	public OperationHandle<Void> getHandleList(String OperationKey) {
		return handleList.get(OperationKey);
	}
}
