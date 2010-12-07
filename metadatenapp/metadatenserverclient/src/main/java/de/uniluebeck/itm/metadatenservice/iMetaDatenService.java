package de.uniluebeck.itm.metadatenservice;

import java.util.List;

import de.uniluebeck.itm.entity.Node;
import de.uniluebeck.itm.metadatacollector.MetaDataCollector;

public interface iMetaDatenService {
	
	
	/**
	 * Fügt dem MetaDatenService einen MetaDatenCollector hinzu, der dann seinerseits
	 * die Daten des Knoten regelmäßig aktualisiert.
	 * @param mdcollector
	 */
	public void addMetaDataCollector(MetaDataCollector mdcollector);
	
	/**
	 * Entfernt  einen MetaDatenCollector aus  dem MetaDatenService.
	 * 
	 * @param mdcollector
	 */
	public void removeMetaDataCollector(MetaDataCollector mdcollector);
	
	/**
	 * Fügt den Knoten dem Verzeichnis hinzu
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
	 * Aktualisiert die Daten des Sensorknotens im Verezeichnis
	 * @param node
	 * @param callback
	 */
	public void updateNode (Node node, final AsyncCallback<String> callback );
	
	

}
