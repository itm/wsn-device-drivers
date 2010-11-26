package de.uniluebeck.itm.metadatenservice;

import java.util.List;

import de.uniluebeck.itm.entity.Node;

public interface MetaDataClient {
	
	public List<Node> search (Node queryexmpl, String query) throws Exception;
	public void searchasync (Node queryexmpl, String query,  final AsyncCallback<List<Node>> callback) throws Exception;

}
