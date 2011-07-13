package de.uniluebeck.itm.wsn.drivers.core;


/**
 * Listener that is called when data is available for a certain <code>Connection</code>.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public interface DataAvailableListener {

	void dataAvailable(ConnectionEvent event);

}
