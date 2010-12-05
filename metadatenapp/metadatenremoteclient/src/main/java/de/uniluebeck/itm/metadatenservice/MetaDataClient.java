package de.uniluebeck.itm.metadatenservice;

import java.util.List;

import de.uniluebeck.itm.entity.Node;

public interface MetaDataClient {
	
	/**
	 * Sucht synchron nach Knoten die dem Beispielknoten entsprechen und/oder nach dem
	 * übergebenen Sql-Query.
	 * @param queryexmpl
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<Node> search (Node queryexmpl, String query) throws Exception;
	
	/**
	 * Sucht asynchron nach Knoten die dem Beispielknoten entsprechen und/oder nach dem
	 * übergebenen Sql-Query.
	 * @param queryexmpl
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public void searchasync (Node queryexmpl, String query,  final AsyncCallback<List<Node>> callback) throws Exception;

}
