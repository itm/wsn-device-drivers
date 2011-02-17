package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;


import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;

public interface IMetaDataCollector {

	public Node collect(String wisemlFile);

}