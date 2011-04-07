package de.uniluebeck.itm.metadaten.server.helper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import de.uniluebeck.itm.metadaten.server.config.ConfigData;
import de.uniluebeck.itm.metadaten.server.config.ObjectFactory;



/**
 * Reads the config file
 * @author Toralf Babel
 *
 */
public class ConfigReader {
	
//	final static String path = "src/main/resources/devices.xml";
	/**Constructor*/
	public ConfigReader(){};
//	public ConfigReader(File source){
//		
//	}
	/**
	 * Delivers the configData read from the configFile
	 * @param path - File that consists of the config
	 * @return ConfigData the read configData read from the given ConfigFile
	 * @throws JAXBException if sth goes wrong with reading the config
	 */
	@SuppressWarnings("unchecked")
	public static ConfigData readConfigFile(final File path) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		final Unmarshaller um = context.createUnmarshaller();
		final JAXBElement<ConfigData> tmp = (JAXBElement<ConfigData>) um.unmarshal(path);
		
		return tmp.getValue();
		
	}
}