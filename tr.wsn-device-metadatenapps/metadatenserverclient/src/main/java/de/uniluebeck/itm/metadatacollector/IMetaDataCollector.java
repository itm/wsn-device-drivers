package de.uniluebeck.itm.metadatacollector;


import de.uniluebeck.itm.entity.Node;

public interface IMetaDataCollector {

	public Node collect(String wisemlFile);

}