package rpc_pro.rpcPrototype.Server.Devices;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ConfigReader {

	final static String path = "src/main/resources/devices.xml";
	
	public static DeviceList readFile() throws JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<DeviceList> tmp = (JAXBElement<DeviceList>) um.unmarshal(new File(path));
		
		return tmp.getValue();
		
	}
}