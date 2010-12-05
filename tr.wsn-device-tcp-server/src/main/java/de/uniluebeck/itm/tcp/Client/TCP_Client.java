package de.uniluebeck.itm.tcp.Client;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class TCP_Client {
	
	public static void main(String[] args) throws Exception {

		/* Gemeinsamer ClientManager */
		Stub stub1 = new Stub("testUser", "testPassword", "localhost", 8080);
		// mehrmaliges login durch aendern des ClientPorts
		//Stub stub2 = new Stub("testUser", "testPassword", "localhost", 8080, 2345);
		Stub stub2 = new Stub("testUser2", "testPassword", "localhost", 8080);
		
		int i=0;
		int j=0;
		
		
		OperationHandle<Void> handle1 = null;
		OperationHandle<Void> handle2 = null;
		OperationHandle<Void> handle3 = null;
		OperationHandle<Void> handle4 = null;
		
		while(true){
			System.out.println("in: ");
			int input = System.in.read();
			
			switch (input){
			
			// 1 druecken
			case 49:
				handle1 = stub1.setMessage("Dies ist Nachricht Nr.: "+i+" von Client1",new AsyncCallback<Void>(){
					@Override
					public void onCancel() {
					}
					@Override
					public void onFailure(Throwable throwable) {
						System.out.println(throwable.getMessage());
					}
					@Override
					public void onSuccess(Void result) {
						System.out.println("Nachricht uebertragen");
					}
					@Override
					public void onProgressChange(float fraction) {
					}});
				i++;
				break;
				
			// 2 druecken
			case 50:
				handle2 = stub1.getMessage(new AsyncCallback<String>(){	
					@Override
					public void onCancel() {
					}
					@Override
					public void onFailure(Throwable throwable) {
						System.out.println(throwable.getMessage());
					}
					@Override
					public void onSuccess(String result) {
						System.out.println(result);
					}
					@Override
					public void onProgressChange(float fraction) {
					}});
				break;
				
			case 51:
				handle3 = stub2.setMessage("Dies ist Nachricht Nr.: "+j+" von Client2",new AsyncCallback<Void>(){
					@Override
					public void onCancel() {
					}
					@Override
					public void onFailure(Throwable throwable) {
						System.out.println(throwable.getMessage());
					}
					@Override
					public void onSuccess(Void result) {
						System.out.println("Nachricht uebertragen");
					}
					@Override
					public void onProgressChange(float fraction) {
					}});
				j++;
				break;	

			case 52:
				handle4 = stub2.getMessage(new AsyncCallback<String>(){
					@Override
					public void onCancel() {
					}
					@Override
					public void onFailure(Throwable throwable) {
						System.out.println(throwable.getMessage());
					}
					@Override
					public void onSuccess(String result) {
						System.out.println(result);
					}
					@Override
					public void onProgressChange(float fraction) {
					}});
				break;
				
//			case 53:
//				// konnte ich nicht sinnvoll testen, da ich keine DeviceBinFile erstellen konnte
//				// vlt ware es sinnvoll hier ein Pfad zu uebergeben, anstatt des DeviceBinFile
//				// sollte aber theoretisch gehen
//				handle1 = stub1.program(null, 0L, new AsyncCallback<Void>(){
//
//					@Override
//					public void onCancel() {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onFailure(Throwable throwable) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onSuccess(Void result) {
//						System.out.println("geht");
//						
//					}
//
//					@Override
//					public void onProgressChange(float fraction) {
//						// TODO Auto-generated method stub
//						
//					}});
//				break;
			//5
			case 53:
				handle1.cancel();
				break;
			//6
			case 54:
				System.out.println(handle1.getState());
				break;
			case 55:
				//handle1.get();
				byte[] bytes = {1,2,3,4,5};
				stub1.program(bytes, 14000, new AsyncCallback<Void>(){

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onFailure(Throwable throwable) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onProgressChange(float fraction) {
						// TODO Auto-generated method stub
						
					}});
				break;
			case 56:
				handle4.get();
				break;
			}
		}

	}

}
