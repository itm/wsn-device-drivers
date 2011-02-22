package de.uniluebeck.itm.tcp.server.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.uniluebeck.itm.tcp.jaxdevices.JaxbDeviceList;
import de.uniluebeck.itm.tcp.jaxdevices.ObjectFactory;
/**
 * Read the device.xml and convert the Elements into JAXB-Objects
 * @author Bjoern Schuett
 *
 */
public class ConfigReader {
	
	/**
	 * Read the device.xml and convert the Elements into JAXB-Objects
	 * @param path the Path of the config-file (devices.xml)
	 * @return a List with the Elements from the devices.xml
	 * @throws JAXBException Error while reading and converting the devices.sml
	 */
	@SuppressWarnings("unchecked")
	public static JaxbDeviceList readFile(final String path) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        final Unmarshaller um = context.createUnmarshaller();
        final JAXBElement<JaxbDeviceList> tmp = (JAXBElement<JaxbDeviceList>) um.unmarshal(new File(path));
		
		return tmp.getValue();
		
	}
}