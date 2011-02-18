package de.uniluebeck.itm.metadatenservice;

import java.io.File;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.MetaDataCollector;

public class Testtreiber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		iMetaDatenService mclient=null;
		
		System.out.println("Init des MetaDatenService");
		try {
			mclient = new MetaDatenService (new File("src/main/resources/config.xml"), new File("src/main/resources/sensors.xml"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Device device= null;
		System.out.println("Collector initialisieren");
//		IMetaDataCollector mcollector = new MetaDataCollector (device, "11"+String.valueOf(new Date().getTime()));
		IMetaDataCollector mcollector = new MetaDataCollector (device, "280120101");
		System.out.println("Collector hinzufügen");
		mclient.addMetaDataCollector(mcollector);
	}

}
