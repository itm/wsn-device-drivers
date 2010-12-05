package de.uniluebeck.itm.metadatenservice;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import de.uniluebeck.itm.entity.Node;
import de.uniluebeck.itm.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadatacollector.MetaDataCollector;

public class MetaDatenService extends TimerTask implements iMetaDatenService{
	
	private ClientStub stub = null;
	private List<MetaDataCollector> collector = new ArrayList<MetaDataCollector> ();
	
	MetaDatenService () throws Exception {
		//TODO Konfig.-Daten aus Configfile auslesen
		stub=new ClientStub("testUser", "testPassword", "localhost", 8080);
		
	}
	
	public static void main(String[] args) throws Exception {

		/*Collection von MetaDatenCollectors*/
		
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
			}
		}
	}
	


	@Override
	public void run() {
		
		for(int i=0; i<collector.size();i++)
		{
			collector.get(i).collect();
		}
		
	}



	@Override
	public void addMetaDataCollector(MetaDataCollector mdcollector) {
		collector.add(mdcollector);
		
	}



	@Override
	public void removeMetaDataCollector(MetaDataCollector mdcollector) {
		collector.remove(mdcollector);
		
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
	public void refreshNode(Node node, AsyncCallback<String> callback) {
		// TODO Clientseitige Implementierung
		
	}



	@Override
	public void updateNode(Node node, AsyncCallback<String> callback) {
		// TODO Clientseitige Implementierung
		
	}



	public List<MetaDataCollector> getCollector() {
		return collector;
	}



	public void setCollector(List<MetaDataCollector> collector) {
		this.collector = collector;
	}

}
