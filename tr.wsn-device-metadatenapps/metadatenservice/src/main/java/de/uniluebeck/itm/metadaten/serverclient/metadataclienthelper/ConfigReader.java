package de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.uniluebeck.itm.metadatenservice.config.ConfigData;
import de.uniluebeck.itm.metadatenservice.config.NodeList;
import de.uniluebeck.itm.metadatenservice.config.ObjectFactory;


public class ConfigReader {
	
//	final static String path = "src/main/resources/devices.xml";
	public ConfigReader(){};
//	public ConfigReader(File source){
//		
//	}
	
	@SuppressWarnings("unchecked")
	public static ConfigData readConfigFile(File path) throws JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<ConfigData> tmp = (JAXBElement<ConfigData>) um.unmarshal(path);
		
		return tmp.getValue();
		
	}
	
	@SuppressWarnings("unchecked")
	public static NodeList readSensorFile(File path) throws JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<NodeList> tmp = (JAXBElement<NodeList>) um.unmarshal(path);
		
		return tmp.getValue();
		
	}
}