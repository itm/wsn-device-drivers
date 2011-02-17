package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;


import java.io.File;

import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;

public interface IMetaDataCollector {

	public Node collect(File wisemlFile);

}