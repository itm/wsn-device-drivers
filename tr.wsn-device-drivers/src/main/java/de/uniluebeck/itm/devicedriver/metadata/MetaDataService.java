package de.uniluebeck.itm.devicedriver.metadata;

/**
 * Interface for classes that manage metadata for sensor devices.
 * 
 * @author Malte Legenhausen
 */
public interface MetaDataService {

	/**
	 * Returns the Metadata as XML representation.
	 * 
	 * @return The meta data as XML object.
	 */
	String getXmlMetaData();
	
	/**
	 * Add the given listener that is called when the meta data has changed.
	 * 
	 * @param listener The listener that has to be added.
	 */
	void addMetaDataListener(MetaDataListener listener);
	
	/**
	 * Remove the given listener from listening.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeMetaDataListener(MetaDataListener listener);
}
