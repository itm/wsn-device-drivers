package de.uniluebeck.itm.metadatenservice;

import java.util.ArrayList;
import java.util.List;


import de.uniluebeck.itm.entity.Node;
import de.uniluebeck.itm.metadatacollector.MetaDatenCollector;

public class MetaDatenService {
	
	public static void main(String[] args) throws Exception {

		/*Collection von MetaDatenCollectors*/
		List<MetaDatenCollector> collector = new ArrayList ();
		
		/* Gemeinsamer ClientManager */
		Stub stub1 = new Stub("testUser", "testPassword", "localhost", 8080);
        Node node = new Node();
        
		node.setId("123");
		node.setIpAddress("192.168.8.102");
		node.setMicrocontroller("TelosB");
		node.setDescription("Solar2000");
		while(true){
			System.out.println("in: ");
			int input = System.in.read();
			
			switch (input){
			
			// 1 druecken
			case 49:
				stub1.add(node, new AsyncCallback<String>(){
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
//				handle1 = stub1.setMessage("Dies ist Nachricht Nr.: "+i+" von Client1",new AsyncCallback<Void>(){
//					@Override
//					public void onCancel() {
//					}
//					@Override
//					public void onFailure(Throwable throwable) {
//						System.out.println(throwable.getMessage());
//					}
//					@Override
//					public void onSuccess(Void result) {
//						System.out.println("Nachricht uebertragen");
//					}
//					@Override
//					public void onProgressChange(float fraction) {
//					}});
//				i++;
				System.out.println("Tue nichts");
				break;
				
			// 2 druecken
			case 50:
//				handle2 = stub1.getMessage(new AsyncCallback<String>(){	
//					@Override
//					public void onCancel() {
//					}
//					@Override
//					public void onFailure(Throwable throwable) {
//						System.out.println(throwable.getMessage());
//					}
//					@Override
//					public void onSuccess(String result) {
//						System.out.println(result);
//					}
//					@Override
//					public void onProgressChange(float fraction) {
//					}});
				System.out.println("Tue nichts");
				break;
				
			case 51:
//				handle3 = stub2.setMessage("Dies ist Nachricht Nr.: "+j+" von Client2",new AsyncCallback<Void>(){
//					@Override
//					public void onCancel() {
//					}
//					@Override
//					public void onFailure(Throwable throwable) {
//						System.out.println(throwable.getMessage());
//					}
//					@Override
//					public void onSuccess(Void result) {
//						System.out.println("Nachricht uebertragen");
//					}
//					@Override
//					public void onProgressChange(float fraction) {
//					}});
//				j++;
				System.out.println("Tue nichts");
				break;	

			case 52:
//				handle4 = stub2.getMessage(new AsyncCallback<String>(){
//					@Override
//					public void onCancel() {
//					}
//					@Override
//					public void onFailure(Throwable throwable) {
//						System.out.println(throwable.getMessage());
//					}
//					@Override
//					public void onSuccess(String result) {
//						System.out.println(result);
//					}
//					@Override
//					public void onProgressChange(float fraction) {
//					}});
				System.out.println("Tue nichts");
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
//			case 53:
//				handle1.cancel();
//				break;
//			//6
//			case 54:
//				System.out.println(handle1.getState());
//				break;
//			case 55:
//				handle1.get();
//				break;
//			case 56:
//				handle4.get();
//				break;
			}
		}

	}

}
