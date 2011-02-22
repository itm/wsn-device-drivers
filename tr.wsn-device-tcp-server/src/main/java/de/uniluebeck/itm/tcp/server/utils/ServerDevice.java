package de.uniluebeck.itm.tcp.server.utils;


import java.io.File;
import java.lang.reflect.Constructor;
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
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.MetaDataCollector;
import de.uniluebeck.itm.metadatenservice.MetaDatenService;
import de.uniluebeck.itm.metadatenservice.iMetaDatenService;
import de.uniluebeck.itm.tcp.jaxdevices.JaxbDevice;
import de.uniluebeck.itm.tcp.jaxdevices.JaxbDeviceList;

/**
 * Create Devices and adds metaDatenCollectors to them
 * @author Andreas Maier
 *
 */
public class ServerDevice {

	/**
	 * the logger.
	 */
	private static Logger log = LoggerFactory.getLogger(ServerDevice.class);
	
	/**
	 * stores the objects representing the devices callable via the server.
	 */
	private static Map<String,DeviceAsync> deviceList = new HashMap<String,DeviceAsync>();

	/**
	 * parameter for activate (true) or deactivate (false, default) the metaDaten-functions
	 */
	private boolean metaDaten = false;
	
	/**
	 * the Path of the config-file (devices.xml)
	 */
	private String devicesPath = "src/main/resources/devices.xml";
	
	/**
	 * the Path of the config-file (config.xml)
	 */
	private String configPath = "src/main/resources/config.xml";
	
	/**
	 * the Path of the config-file (sensors.xml)
	 */
	private String sensorsPath = "src/main/resources/sensors.xml";
	
	/**
	 * The metaDatenService
	 */
	private iMetaDatenService mclient = null;
	
	/**
	 * Constructor.
	 */
	public ServerDevice(){
	}
	
	/**
	 * Constructor.
	 * @param devicesPath the Path of the config-file (devices.xml), "" for standard-Path
	 * @param configPath the Path of the config-file (config.xml), "" for standard-Path
	 * @param sensorsPath the Path of the config-file (sensors.xml), "" for standard-Path
	 * @param meta parameter for activate (true) or deactivate (false, default) the metaDaten-functions
	 */
	public ServerDevice(final String devicesPath, final String configPath, final String sensorsPath, final boolean meta){
		if(!devicesPath.equalsIgnoreCase("")){
			this.devicesPath = devicesPath;
		}
		if(!configPath.equalsIgnoreCase("")){
			this.configPath = configPath;
		}
		if(!sensorsPath.equalsIgnoreCase("")){
			this.sensorsPath = sensorsPath;
		}
		this.metaDaten = meta;
	}
	
	/**
	 * reads the devices.xml, creates a device object for every device in the file
	 * and store it in the DeviceList.
	 */
	public void createServerDevices() {
		
		try {
			if(metaDaten){
				mclient = new MetaDatenService (new File(configPath),new File(sensorsPath));
			}
			final JaxbDeviceList list = readDevices(devicesPath);
			
			for(JaxbDevice jaxDevice : list.getJaxbDevice()){
				final String key = createID(jaxDevice);
				final Connection con = createConnection(jaxDevice.getConnectionType());
				final Device<?> device = createDevice(jaxDevice.getDeviceType(), con);
				
				if(metaDaten){
					final IMetaDataCollector mcollector = new MetaDataCollector (device, key);
					mclient.addMetaDataCollector(mcollector);
				}
				
				con.connect(jaxDevice.getPort());
				final DeviceAsync deviceAsync = createDeviceAsync(device);
				deviceList.put(key, deviceAsync);
				
			}
			
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			System.exit(-1);
		}
	}
	
	/**
	 * readouts the devices specified in the devices.xml.
	 * @param path the Path of the config-file (devices.xml)
	 * @return a JaxbDeviceList object with a list of devices in it.
	 * @throws JAXBException Error while try to read the devices.xml
	 */
	private JaxbDeviceList readDevices(final String path) throws JAXBException{
		
		return ConfigReader.readFile(path);
		
	}
	//ConnectionType = de.uniluebeck.itm.devicedriver.mockdevice.MockConnection
	/**
	 * creates a connection instance of the type specified in the devices.xml .
	 * @param connectionType the Type of the Connection
	 * @return Connection for the Device
	 */
	private Connection createConnection(final String connectionType) {
		
		Connection connection = null;
		Class<?> con;
		
		try {
			con = Class.forName(connectionType);
			connection = (Connection) con.newInstance();
		} catch (final Exception e) {
			log.error(e.getMessage());
		}
		return  connection;
	}
	
	//DeviceName = de.uniluebeck.itm.devicedriver.mockdevice.MockDevice
	//DeviceName = de.uniluebeck.itm.devicedriver.jennic.JennicDevice
	/**
	 * creates a device instance of the type specified in the devices.xml .
	 * @param deviceName the Name of the DeviceType
	 * @param con the connection for this Device
	 * @return Device-Instance
	 */
	private Device<?> createDevice(final String deviceName, final Connection con) {

		Device<?> device = null;
		Class<?> deviceClass;
		
		try {
			deviceClass = Class.forName(deviceName);
			
			for(Constructor<?> constructor : deviceClass.getConstructors()){
				final Class<?>[] types = constructor.getParameterTypes();
				if (types.length == 1) {
					final Class<?> type = types[0];
					if(type.isInstance(con)){
						device = (Device<?>) constructor.newInstance(new Object[] {con});
						break;
					}
				}
			}
		} catch (final Exception e) {
			log.error("Unable to create device: " + deviceName, e);
			System.exit(-1);
		}
	    
		return device;
	}
	
	/**
	 * creates the actual object representing the device on the server.
	 * @param device the device that shall be usable asynchronously.
	 * @return a representation of the device which can be called asynchronously.
	 */
	private DeviceAsync createDeviceAsync(final Device<?> device){
		
		return new QueuedDeviceAsync(new PausableExecutorOperationQueue(), device);
	}
	
	/**
	 * creates a distinct id for the devices.
	 * @param jaxDevice the Device for which the Id should be created
	 * @return the Id for a Device
	 */
	private String createID(final JaxbDevice jaxDevice){
		
		if(jaxDevice.getDeviceId() != null){
			return jaxDevice.getDeviceId();
		}
		
		int rand;
		final int number = 1000;
		do{
			rand = (int) (Math.random()*number)%number;
		}while(deviceList.containsKey(String.valueOf(rand)));
		
		return String.valueOf(rand);
	}
	/**
	 * get the List with the Devices
	 * @return List with the Devices
	 */
	public Map<String, DeviceAsync> getDeviceList() {
		return deviceList;
	}
}
