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
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.ConfigReader;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.JaxbDevice;
import de.uniluebeck.itm.tcp.Server.JaxbDevices.JaxbDeviceList;

public class ServerDevice {

	private static Logger log = LoggerFactory.getLogger(ServerDevice.class);
	
	private static Map<String,DeviceAsync> DeviceList = new HashMap<String,DeviceAsync>();

	ServerDevice(){
	}
	
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
	
	private JaxbDeviceList readDevices() throws JAXBException{
		
		return ConfigReader.readFile();
		
	}
	//ConnectionType = de.uniluebeck.itm.devicedriver.mockdevice.MockConnection
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
	
	private DeviceAsync createDeviceAsync(Device device){
		
		return new QueuedDeviceAsync(new SingleThreadOperationQueue(), device);
	}
	
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
