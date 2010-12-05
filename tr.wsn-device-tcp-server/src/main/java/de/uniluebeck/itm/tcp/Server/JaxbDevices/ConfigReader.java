package de.uniluebeck.itm.tcp.Server.JaxbDevices;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ConfigReader {

	final static String path = "src/main/resources/devices.xml";
	
	public static JaxbDeviceList readFile() throws JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<JaxbDeviceList> tmp = (JAXBElement<JaxbDeviceList>) um.unmarshal(new File(path));
		
		return tmp.getValue();
		
	}
}