package de.uniluebeck.itm.tcp.server.utils;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadatenservice.MetaDatenService;
import de.uniluebeck.itm.metadatenservice.iMetaDatenService;

/**
 * Thread for connecting to the MetadataService
 * @author Andreas Maier
 * @author Bjoern Schuett
 *
 */
public class MetaDataThread extends Thread {
	
	/**
	 * the logger.
	 */
	private static Logger log = LoggerFactory.getLogger(MetaDataThread.class);
	
	/**
	 * time to wait between the connections attemps
	 */
	private final static int WAITTIME = 600000;
	
	/**
	 * 
	 */
	private boolean running = true;
	
	/**
	 * The metaDatenService
	 */
	private iMetaDatenService mclient = null;
	
	/**
	 * the Path of the config-file (config.xml)
	 */
	private File configPath = null;
	
	/**
	 * the Path of the config-file (sensors.xml)
	 */
	private File sensorsPath = null;
	/**
	 * a List with the MetaDataCollectors for every existing Device
	 */
	private List<IMetaDataCollector> collectorList = null;
	
	/**
	 * Constructor .
	 * @param configPath the Path of config.xml
	 * @param sensorsPath the Path of sensors.xml
	 * @param collectorList a List with the MetaDataCollectors for every existing Device
	 */
	public MetaDataThread(final File configPath, final File sensorsPath, final List<IMetaDataCollector> collectorList){
		this.configPath = configPath;
		this.sensorsPath = sensorsPath;
		this.collectorList = collectorList;
	}
	
	@Override
	public void run(){
		while(running){
			
			try{
				mclient = new MetaDatenService (configPath,sensorsPath);
				for(IMetaDataCollector mcollector : collectorList){
					mclient.addMetaDataCollector(mcollector);
				}
				running = false;
			}catch(final Exception ex){
				log.info("No Metadatenservice were found, the Server will try to connect in 10 Minutes again");
				log.error("No Metadatenservice were found, the Server will try to connect in 10 Minutes again",ex);
				try {
					Thread.sleep(WAITTIME);
				} catch (final InterruptedException e) {
					log.error("",e);
				}
			}
		}
	}
}