package de.uniluebeck.itm.metadatenservice;

import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;

public interface iMetaDatenService {
	//TODO i gross
	
	/**
	 * Fuegt dem MetaDatenService einen MetaDatenCollector hinzu, der dann seinerseits
	 * die Daten des Knoten regelmaessig aktualisiert.
	 * @param mdcollector
	 */
	public void addMetaDataCollector(IMetaDataCollector mdcollector);
	
	/**
	 * Entfernt  einen MetaDatenCollector aus  dem MetaDatenService.
	 * 
	 * @param mdcollector
	 */
	public void removeMetaDataCollector(IMetaDataCollector mdcollector);
	
	/**
	 * Fuegt den Knoten dem Verzeichnis hinzu
	 * @param node
	 * @param callback
	 */
	public void addNode (Node node, final AsyncCallback<String> callback );
	
	/**
	 * Entfernt den Sensorknoten aus dem Verzeichnis
	 * @param node
	 * @param callback
	 */
	public void removeNode (Node node, final AsyncCallback<String> callback );
	
	/**
	 * Aktualisiert den TimeStamp des Sensorknotens im Verezeichnis
	 * @param node
	 * @param callback
	 */
	public void refreshNode (Node node, final AsyncCallback<String> callback );

	/**
	 * Aktualisiert den TimeStamp des Sensorknotens im Verzeichnis
	 * nutzt das synchrone Interface
	 * @param node
	 */
	void refreshNodeSync(Node node);

	/**
	 * Entfernt alle Daten des aktuellen TCP-Servers, in dem der Metadatenservice laeuft,
	 * aus dem Metadatenverzeichnis
	 */
	void removeData();
	
	
	

}
