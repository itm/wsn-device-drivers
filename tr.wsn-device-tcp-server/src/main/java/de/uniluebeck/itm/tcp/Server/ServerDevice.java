package de.uniluebeck.itm.tcp.Server;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.ConfigReader;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.JaxbDevice;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.JaxbDeviceList;

public class ServerDevice {

	/**
	 * the logger.
	 */
	private static Logger log = LoggerFactory.getLogger(ServerDevice.class);
	
	/**
	 * stores the objects representing the devices callable via the server.
	 */
	private static Map<String,DeviceAsync> DeviceList = new HashMap<String,DeviceAsync>();

	/**
	 * Constructor.
	 */
	ServerDevice(){
	}
	
	/**
	 * reads the devices.xml, creates a device object for every device in the file
	 * and store it in the DeviceList.
	 */
	public void createServerDevices() {
		
		try {
			JaxbDeviceList list = readDevices();
			
			for(JaxbDevice jaxDevice : list.getJaxbDevice()){
				//String key = createID();
				
				String key="1"; //TODO nur zum testen
				//Connection con = createConnection(jaxDevice.getKnotenTyp());
				Connection con = createConnection(jaxDevice.getConnectionType());
				con.connect(jaxDevice.getPort());
				Device device = createDevice(jaxDevice.getDeviceType(), con);
				DeviceAsync deviceAsync = createDeviceAsync(device);
				DeviceList.put(key, deviceAsync);
				
			}
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		System.out.println();
		
	}
	
	/**
	 * readouts the devices specified in the devices.xml.
	 * @return a JaxbDeviceList object with a list of devices in it.
	 * @throws JAXBException
	 */
	private JaxbDeviceList readDevices() throws JAXBException{
		
		return ConfigReader.readFile();
		
	}
	//ConnectionType = de.uniluebeck.itm.devicedriver.mockdevice.MockConnection
	/**
	 * creates a connection instance of the type specified in the devices.xml .
	 */
	@SuppressWarnings("unchecked")
	private Connection createConnection(String ConnectionType) {
		
		Connection connection = null;
		Class con;
		
		try {
			con = Class.forName(ConnectionType);
			connection = (Connection) con.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  connection;
	}
	
	//DeviceName = de.uniluebeck.itm.devicedriver.mockdevice.MockDevice
	//DeviceName = de.uniluebeck.itm.devicedriver.jennic.JennicDevice
	/**
	 * creates a device instance of the type specified in the devices.xml .
	 */
	@SuppressWarnings("unchecked")
	private Device createDevice(String DeviceName, Connection con) {

		Device device = null;
		Class deviceClass;
		
		try {
			deviceClass = Class.forName(DeviceName);
			Constructor ConncectionArgsConstructor = deviceClass.getConstructor(new Class[] {con.getClass()});
			device = (Device) ConncectionArgsConstructor.newInstance(new Object[] {con});
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return device;
	}
	
	/**
	 * creates the actual object representing the device on the server.
	 * @param device the device that shall be usable asynchronously.
	 * @return a representation of the device which can be called asynchronously.
	 */
	private DeviceAsync createDeviceAsync(Device device){
		
		return new QueuedDeviceAsync(new PausableExecutorOperationQueue(), device);
	}
	
	/**
	 * creates a distinct id for the devices.
	 * @return
	 */
	private String createID(){
		
		double rand;
		
		do{
			rand = Math.random()%1000;
		}while(DeviceList.containsKey(String.valueOf(rand)));
		
		return String.valueOf(rand);
	}
	
	public Map<String, DeviceAsync> getDeviceList() {
		return DeviceList;
	}
}
