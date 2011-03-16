package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.io.File;

import de.uniluebeck.itm.metadatenservice.config.Node;

/**
 * Interface for the metadata collector. Here all possible additional
 * information according metadata is collected. There are two collectors that
 * collect data: 
 * 1. FileCollector collects data from the given sensors.xml -file
 * 2. DeviceCollector collects data from the sensor node itself
 * 
 * @author Toralf Babel
 * 
 */
public interface IMetaDataCollector {
	/**
	 * 
	 * @param sensorFile
	 * @return Node with collected information added
	 */
	public Node collect(File sensorFile);

}