package de.uniluebeck.itm.metadaten.server.helper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.NodeList;

import de.uniluebeck.itm.metadaten.server.config.ConfigData;
import de.uniluebeck.itm.metadaten.server.config.ObjectFactory;



/**
 * Reads the config file
 * @author babel
 *
 */
public class ConfigReader {
	
//	final static String path = "src/main/resources/devices.xml";
	public ConfigReader(){};
//	public ConfigReader(File source){
//		
//	}
	/**
	 * Delivers the configData read from the configFile
	 */
	@SuppressWarnings("unchecked")
	public static ConfigData readConfigFile(final File path) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		final Unmarshaller um = context.createUnmarshaller();
		final JAXBElement<ConfigData> tmp = (JAXBElement<ConfigData>) um.unmarshal(path);
		
		return tmp.getValue();
		
	}
	
	@SuppressWarnings("unchecked")
	public static NodeList readSensorFile(final File path) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		final Unmarshaller um = context.createUnmarshaller();
		final JAXBElement<NodeList> tmp = (JAXBElement<NodeList>) um.unmarshal(path);
		
		return tmp.getValue();
		
	}
}