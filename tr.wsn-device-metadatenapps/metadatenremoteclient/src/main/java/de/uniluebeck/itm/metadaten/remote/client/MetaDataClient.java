package de.uniluebeck.itm.metadaten.remote.client;

import java.util.List;

import de.uniluebeck.itm.metadaten.remote.entity.Node;

/**
 * @author babel
 * The MetaDataClient can be used for searching for nodes in the 
 * MetaDataDirectory
 */
public interface MetaDataClient {
	
	/**
	 * Sucht synchron nach Knoten die dem Beispielknoten entsprechen und/oder nach dem
	 * Uebergebenen Sql-Query.
	 * @param queryexmpl
	 * @param query
	 * @return Delivers a List of nodes matching the search conditions
	 * @throws Exception
	 */
	public List<Node> search (Node queryexmpl, String query) throws Exception;
	
	/**
	 * Sucht asynchron nach Knoten die dem Beispielknoten entsprechen und/oder nach dem
	 * Uebergebenen Sql-Query.
	 * @param queryexmpl
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public void searchasync (Node queryexmpl, String query,  final AsyncCallback<List<Node>> callback) throws Exception;

}
