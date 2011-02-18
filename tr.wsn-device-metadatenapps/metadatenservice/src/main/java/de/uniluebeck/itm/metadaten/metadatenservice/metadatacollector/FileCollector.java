package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.io.File;

import javax.xml.bind.JAXBException;

import de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper.ConfigReader;
import de.uniluebeck.itm.metadatenservice.config.Capability;
import de.uniluebeck.itm.metadatenservice.config.Node;
import de.uniluebeck.itm.metadatenservice.config.NodeList;

public class FileCollector {

	public FileCollector() {
	};

	public static void main(String[] args) throws Exception {

		// Node node = new Node();
		// node.setId("Test281220101");
		// TODO Classloader
		// FileCollector collect = new FileCollector();
		// node= collect.filecollect(node, "sensors.xml");
		// System.out.println("Ergbenis"+ node.getDescription());
	}

	/**
	 * Collects the information given by the wiseml-configfile
	 * 
	 * @param node
	 * @param wisemlurl
	 * @return node - with supplemented information
	 */
	public Node filecollect(Node node, File source) {

		NodeList nodelist = null;
		try {
			nodelist = ConfigReader.readSensorFile(source);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		for (Node result : nodelist.getNode()) {
			if (result.getNodeid().equals(node.getNodeid())) {
				if (node.getDescription() == null) {
					if (!(result.getDescription() == null)) {
						node.setDescription(result.getDescription());
					}
				}
				if (node.getPort() == null) {
					if (result.getPort().shortValue() != 0) {
						node.setPort(result.getPort());
					}
				}
				if (node.getMicrocontroller() == null) {
					if (!(result.getMicrocontroller() == null)) {
						node.setMicrocontroller(result.getMicrocontroller());
					}
				}
				for (Capability cap : result.getCapability()) {
					node.getCapability().add(cap);
				}
			}
		}
		return node;
	}

}
