package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper.ConfigReader;
import de.uniluebeck.itm.metadatenservice.config.Capability;
import de.uniluebeck.itm.metadatenservice.config.Node;
import de.uniluebeck.itm.metadatenservice.config.NodeList;

/**
 * Reads all possible MetaData for a sensornode from the given sensor.xml - File
 * 
 * @author Toralf Babel
 * 
 */
public class FileCollector {
	/**
	 * Logger for FileCollector
	 */
	private static Logger log = LoggerFactory.getLogger(FileCollector.class);
	/** Constructor */
	public FileCollector() {
	};

	/**
	 * Collects the information given by the sensor.xml - file
	 * 
	 * @param node
	 *            node with current collected information -> at least the id to
	 *            search for in the config - file
	 * @param source - the file with the information for the sensor node
	 * @return node - with supplemented information
	 */
	public Node filecollect(final Node node, final File source) {

		NodeList nodelist = null;
		try {
			nodelist = ConfigReader.readSensorFile(source);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		try {
			for (Node result : nodelist.getNode()) {
				if (result.getNodeid().equals(node.getNodeid())) {
					if (node.getDescription() == null) {
						if (!(result.getDescription() == null)) {
							node.setDescription(result.getDescription());
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
		}
		catch(final NullPointerException e){
			log.error(e.getMessage(),e);
		}
		return node;
	}

}
