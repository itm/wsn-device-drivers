package de.uniluebeck.itm.metadatenservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.uniluebeck.itm.entity.Node;
import de.uniluebeck.itm.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadatacollector.MetaDataCollector;

public class MetaDatenService extends TimerTask implements iMetaDatenService{
	
	private ClientStub stub = null;
	private List<IMetaDataCollector> collector = new ArrayList<IMetaDataCollector> ();
	Timer timer = new Timer();
	
	MetaDatenService () throws Exception {
		//TODO Konfig.-Daten aus Configfile auslesen
		stub=new ClientStub("testUser", "testPassword", "localhost", 8080);
		
	    // nach 2 Sek geht’s los
//	      timer.schedule  ( new Task(), 2000 );
		
	    // nach 1 Sek geht’s los und dann alle 5 Sekunden
	      timer.schedule  ( this, 1000, 5000 );
	}
	
//	public static void main(String[] args) throws Exception {
//
//		/*Collection von MetaDatenCollectors*/
//		
//		/* Gemeinsamer ClientManager */
//		Stub stub1 = new Stub("testUser", "testPassword", "localhost", 8080);
//        Node node = new Node();
//        
//		node.setId("123");
//		node.setIpAddress("192.168.8.102");
//		node.setMicrocontroller("TelosB");
//		node.setDescription("Solar2000");
//		while(true){
//			System.out.println("in: ");
//			int input = System.in.read();
//			
//			switch (input){
//			
//			// 1 druecken
//			case 49:
//				stub1.add(node, new AsyncCallback<String>(){
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
//						
//					}
//					@Override
//					public void onProgressChange(float fraction) {
//					
//				}});
//			}
//		}
//	}
//	


	@Override
	public void run() {
		
		for(int i=0; i<collector.size();i++)
		{
			System.out.println("Die Knotens mit ID: " +collector.get(i).collect().getId()+"wird dem Verzeichnis hinzugefügt");
			refreshNode(collector.get(i).collect(), new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(throwable.getMessage());
			}
			@Override
			public void onSuccess(String result) {	
			}
			@Override
			public void onProgressChange(float fraction) {
			
		}});
		}
		
	}



	@Override
	public void addNode(Node node, final AsyncCallback<String> callback) {
		stub.add(node, new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				callback.onFailure(throwable);
			}
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
				
			}
			@Override
			public void onProgressChange(float fraction) {
			
		}});
		
	}



	@Override
	public void removeNode(Node node, AsyncCallback<String> callback) {
		// TODO Clientseitige Implementierung
		
	}



	@Override
	public void refreshNode(Node node,final AsyncCallback<String> callback) {
		// TODO Ersetzt die Methode UpdateNode?
		stub.add(node, new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				callback.onFailure(throwable);
			}
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
				
			}
			@Override
			public void onProgressChange(float fraction) {
			
		}});
	}



	@Override
	public void updateNode(Node node, final AsyncCallback<String> callback) {
		// TODO Updatemethode im Stub implementieren
		stub.add(node, new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				callback.onFailure(throwable);
			}
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
				
			}
			@Override
			public void onProgressChange(float fraction) {
			
		}});
	}



	public List<IMetaDataCollector> getCollector() {
		return collector;
	}



	public void setCollector(List<IMetaDataCollector> collector) {
		this.collector = collector;
	}

	@Override
	public void addMetaDataCollector(IMetaDataCollector mdcollector) {
		collector.add(mdcollector);
		
	}

	@Override
	public void removeMetaDataCollector(IMetaDataCollector mdcollector) {
		collector.remove(mdcollector);
	}

}
