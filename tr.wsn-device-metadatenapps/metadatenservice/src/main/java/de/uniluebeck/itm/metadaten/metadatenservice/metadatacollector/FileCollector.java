package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;
import de.uniluebeck.itm.metadaten.metadatenservice.entity.Setup;

public class FileCollector {

	public FileCollector() {
	};
	public static void main(String[] args) throws Exception {
		
	 Node node = new Node();
	 node.setId("Test281220101");
	 //TODO Classloader
//	 FileCollector collect = new FileCollector();
//		node= collect.filecollect(node, "sensors.xml");
//		System.out.println("Ergbenis"+ node.getDescription());
	}

	/**
	 * Collects the information given by the wiseml-configfile
	 * 
	 * @param node
	 * @param wisemlurl
	 * @return node - with supplemented information
	 */
	public Node filecollect(Node node, File source) {
		// TODO Informationen Ueberschreiben? IP zum Beispiel ja eher nicht
		Serializer serializer = new Persister();
		Setup setup = new Setup();
		try {
			setup = serializer.read(de.uniluebeck.itm.metadaten.metadatenservice.entity.Setup.class,
					source);
			// serializer.read(ConfigData, source);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Node result : setup.getNodeList()) {
			if (result.getId().equals(node.getId())) {
				if (node.getDescription().equals("")) {
					if (!(result.getDescription() == null)) {
						node.setDescription(result.getDescription());
					}
				}
				if (node.getPort() == 0) {
					if (result.getPort() != 0) {
						node.setPort(result.getPort());
					}
				}
				if (node.getMicrocontroller().equals("")) {
					if (!(result.getMicrocontroller() == null)) {
						node.setMicrocontroller(result.getMicrocontroller());
					}
				}

				node.setCapabilityList(result.getCapabilityList());
			}
		}
		return node;
	}

}
