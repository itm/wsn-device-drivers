package de.uniluebeck.itm.devicedriver.metadata;

/**
 * Listener for meta data listing.
 * 
 * @author Malte Legenhausen
 */
public interface MetaDataListener {

	/**
	 * 
	 */
	void onMetaDataChanged(String path, String value);
}
