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
	int count=0;
	
	MetaDatenService () throws Exception {

//		ConfigData config = loadConfig("C:\\uni hl\\workspace\\fallstudie2010\\sources\\tr.wsn-device-metadatenapps\\metadatenserverclient\\src\\main\\java\\resources\\config.xml");
		config = loadConfig("config.xml");
		
		stub=new ClientStub(config.getUsername(), config.getPassword(), config.getServerIP(), config.getServerPort(), config.getClientport());
		
	    // nach 2 Sek geht’s los
//	      timer.schedule  ( new Task(), 2000 );
		
	    // nach 1 Sek geht’s los und dann alle 5 Sekunden
	      timer.schedule  ( this, 10000, 5000 );
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
		log.info("Start Refreshrun: connecten");
		count = 0;
		stub.connect(config.getUsername(), config.getPassword());
//		stub.connect(config.getUsername(), config.getPassword(),new AsyncCallback<String>() {
//			
//			@Override
//			public void onCancel() {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
//				System.out.println(throwable.getMessage());
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				System.out.println(result);
//			}
//
//			@Override
//			public void onProgressChange(float fraction) {
//			}
//		});
		log.info("Refreshrun connected");
		for(int i=0; i<collector.size();i++)
		{
			System.out.println("Der Knoten mit ID: " +collector.get(i).collect(config.getWisemlFile()).getId()+"wird dem Verzeichnis hinzugefügt");
			refreshNodeSync(collector.get(i).collect(config.getWisemlFile()));
//			refreshNode(collector.get(i).collect(config.getWisemlFile()), new AsyncCallback<String>(){
//			@Override
//			public void onCancel() {
//			}
//			@Override
//			public void onFailure(Throwable throwable) {
//				count++;
//				log.error((throwable.getMessage()));
//			}
//			@Override
//			public void onSuccess(String result) {
//				count++;
//				log.info(new Date() + " Node  updated");
//			}
//			@Override
//			public void onProgressChange(float fraction) {
//			
//		}});
		}
//		while (count < collector.size()){
//			System.out.println("Warte auf Ergebnisse");
//		}
		stub.disconnect();
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
		log.debug("ConfigFile:" + source.getName() + source.toString());
		 try {
			config = serializer.read(de.uniluebeck.itm.metadaten.metadatenservice.entity.ConfigData.class, source);
//			serializer.read(ConfigData, source);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.debug("Config:" + config.getPassword() + config.getServerIP() + config.getUsername() + config.getServerPort() + config.getClientport());
		return config;
	}
	/**
	 * Adds an node to the directory - no existing connection needed
	 */
	@Override
	public void addNode(Node node, final AsyncCallback<String> callback) {
//		stub.connect(config.getUsername(), config.getPassword());
		stub.add(node, new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				callback.onFailure(throwable);
//				stub.disconnect();
			}
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
//				stub.disconnect();
				
			}
			@Override
			public void onProgressChange(float fraction) {
			
		}});
	}



	@Override
	public void removeNode(Node node, AsyncCallback<String> callback) {
		// TODO Clientseitige Implementierung
		
	}



	/**
	 * Refreshes the Nodeentry in the directory - Needs a existing connection to the server
	 */
	@Override
	public void refreshNode(Node node, final AsyncCallback<String> callback) {
		stub.refresh(node, new AsyncCallback<String>(){
			@Override
			public void onCancel() {
			}
			@Override
			public void onFailure(Throwable throwable) {
				System.err.println(throwable.getMessage());
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
	/**
	 * Refreshes the Nodeentry in the directory - Needs a existing connection to the server
	 * Uses sync-Operation
	 */
	@Override
	public void refreshNodeSync(Node node) {
		stub.refreshSync(node);
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
//		stub.connect(config.getUsername(), config.getPassword());
//		stub.add(mdcollector.collect(config.getWisemlFile()), new AsyncCallback<String>(){
//			@Override
//			public void onCancel() {
//			}
//			@Override
//			public void onFailure(Throwable throwable) {
//				System.out.println(throwable.getMessage());
////				stub.disconnect();
//			}
//			@Override
//			public void onSuccess(String result) {	
//				log.info("Gesendet");
//				stub.disconnect();
//				log.info("Gesendet und getrennt");
//			}
//			@Override
//			public void onProgressChange(float fraction) {
//			
//		}});
		log.info("und wieder neu connecten");
	}

	@Override
	public void removeMetaDataCollector(IMetaDataCollector mdcollector) {
		collector.remove(mdcollector);
	}

}
