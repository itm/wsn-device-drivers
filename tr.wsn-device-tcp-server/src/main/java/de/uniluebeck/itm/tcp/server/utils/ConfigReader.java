package de.uniluebeck.itm.tcp.server.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.uniluebeck.itm.tcp.jaxdevices.JaxbDeviceList;
import de.uniluebeck.itm.tcp.jaxdevices.ObjectFactory;

public class ConfigReader {

	final static String path = "src/main/resources/devices.xml";
	
	@SuppressWarnings("unchecked")
	public static JaxbDeviceList readFile() throws JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<JaxbDeviceList> tmp = (JAXBElement<JaxbDeviceList>) um.unmarshal(new File(path));
		
		return tmp.getValue();
		
	}
}