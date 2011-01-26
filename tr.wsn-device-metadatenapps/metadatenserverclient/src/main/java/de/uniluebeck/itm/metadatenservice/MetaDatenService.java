package de.uniluebeck.itm.metadatenservice;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.metadaten.metadatenservice.entity.Capability;
import de.uniluebeck.itm.metadaten.metadatenservice.entity.ConfigData;
import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;


public class MetaDatenService extends TimerTask implements iMetaDatenService{
	
	private static Log log = LogFactory.getLog(MetaDatenService.class);
	private ClientStub stub = null;
	private List<IMetaDataCollector> collector = new ArrayList<IMetaDataCollector> ();
	ConfigData config = new ConfigData();
	Timer timer = new Timer();
	
	MetaDatenService () throws Exception {

//		ConfigData config = loadConfig("C:\\uni hl\\workspace\\fallstudie2010\\sources\\tr.wsn-device-metadatenapps\\metadatenserverclient\\src\\main\\java\\resources\\config.xml");
		config = loadConfig("config.xml");
		
		stub=new ClientStub(config.getUsername(), config.getPassword(), config.getServerIP(), config.getPort());
		
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
//		ClientStub stub1 = new ClientStub("testUser", "testPassword", "localhost", 8080);
//        Node node = new Node();
//        
//		node.setId("1237");
//		node.setIpAddress("192.168.8.102");
//		node.setMicrocontroller("TelosB");
//		node.setDescription("Solar2000");
//
//		node.setIpAddress("192.168.8.102");
//		node.setMicrocontroller("TelosB");
//		node.setDescription("Solar2002");
//		node.setTimestamp(new Date());
//		Capability cap = new Capability ();
//		Capability cap2 = new Capability ();
//		cap.setDatatype("int");
//		cap.setName("Temperatur");
//		cap.setNode(node);
//		cap.setUnit("Grad Fahre");
////		cap.setId(1);
//		List <Capability> capList = new ArrayList <Capability>();
//		capList.add(cap);
//		cap2.setDatatype("double");
//		cap2.setName("Licht");
//		cap2.setUnit("Luchs");
////		cap2.setId(2);
//		cap2.setNode(node);
//		capList.add(cap2);
//		node.setCapabilityList(capList);
//		node.setPort((short)1234);
//		node.setTimestamp(new Date());
//		
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
//			
//		
//	}
	


	@Override
	public void run() {
		
		for(int i=0; i<collector.size();i++)
		{
			System.out.println("Die Knotens mit ID: " +collector.get(i).collect(config.getWisemlFile()).getId()+"wird dem Verzeichnis hinzugefügt");
			refreshNode(collector.get(i).collect(config.getWisemlFile()), new AsyncCallback<String>(){
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

	public void writeConfig(ConfigData config){
		File result = new File("configschreiben.xml");
		Serializer serial=new Persister();
		try {
			serial.write(config, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Load of  ConfigData needed for communication
	 * @param fileurl
	 * @return
	 */
	public ConfigData loadConfig(String fileurl){
		ConfigData config = new ConfigData();
		Serializer serializer = new Persister();
		URI fileuri = null;
		try {
			fileuri = ClassLoader.getSystemResource(fileurl).toURI();
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		File source = new File(fileuri);
		System.out.println("File:" + source.getName() + source.toString());
		 try {
			config = serializer.read(de.uniluebeck.itm.metadaten.metadatenservice.entity.ConfigData.class, source);
//			serializer.read(ConfigData, source);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Config:" + config.getPassword() + config.getServerIP() + config.getUsername() + config.getPort() + config.getClientport());
		return config;
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



//	@Override
//	public void refreshNode(Node node,final AsyncCallback<String> callback) {
//		// TODO Ersetzt die Methode UpdateNode?
//		stub.add(node, new AsyncCallback<String>(){
//			@Override
//			public void onCancel() {
//			}
//			@Override
//			public void onFailure(Throwable throwable) {
//				callback.onFailure(throwable);
//			}
//			@Override
//			public void onSuccess(String result) {
//				callback.onSuccess(result);
//				
//			}
//			@Override
//			public void onProgressChange(float fraction) {
//			
//		}});
//	}



	@Override
	public void refreshNode(Node node, final AsyncCallback<String> callback) {
		// TODO Updatemethode im Stub implementieren
		stub.refresh(node, new AsyncCallback<String>(){
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
		stub.add(mdcollector.collect(config.getWisemlFile()), new AsyncCallback<String>(){
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

	@Override
	public void removeMetaDataCollector(IMetaDataCollector mdcollector) {
		collector.remove(mdcollector);
	}

}
