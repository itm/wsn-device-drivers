package de.uniluebeck.itm.tcp.server.utils;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.IMetaDataCollector;
import de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector.MetaDataCollector;
import de.uniluebeck.itm.tcp.jaxdevices.JaxbDevice;
import de.uniluebeck.itm.tcp.jaxdevices.JaxbDeviceList;
import de.uniluebeck.itm.tcp.jaxdevices.ObjectFactory;
import de.uniluebeck.itm.tcp.server.exceptions.DuplacateIdException;
import de.uniluebeck.itm.tcp.server.exceptions.EmptyIdException;

/**
 * Create Devices and adds metaDatenCollectors to them
 * @author Andreas Maier
 * @author Bjoern Schuett
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
	private String devicesPath = "";
	
	/**
	 * the Path of the config-file (config.xml)
	 */
	private String configPath = "";
	
	/**
	 * the Path of the config-file (sensors.xml)
	 */
	private String sensorsPath = "";	
	
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
		this.devicesPath = devicesPath;
		this.configPath = configPath;
		this.sensorsPath = sensorsPath;
		this.metaDaten = meta;
	}
	
	/**
	 * reads the devices.xml, creates a device object for every device in the file
	 * and store it in the DeviceList.
	 */
	public void createServerDevices() {
		
		final List<IMetaDataCollector> collectorList = new ArrayList<IMetaDataCollector>();
		
		try {
			/* erzeugen der JAXB-Device-Objekte zu den Eintraegen in der devices.xml */
			final JaxbDeviceList list = readDevices(devicesPath);
			
			for(JaxbDevice jaxDevice : list.getJaxbDevice()){
				/* erstellen oder auslesen der einzigartigen DeviceID */
				final String key = createID(jaxDevice);
				/* erstellen einer Device-spezifischen Connection */
				final Connection con = createConnection(jaxDevice.getConnectionType());
				/* erstellen einer repraesentation eines physikalischen Devices */
				final Device<?> device = createDevice(jaxDevice.getDeviceType(), con);
				/* erstellen eines MetaDataCollector fuer das Device, wenn MetaDataService aktiviert ist */
				if(metaDaten){
					collectorList.add(new MetaDataCollector (device, key));
				}
				/* herstellen einer physikalischen Verbindung zum Device */
				con.connect(jaxDevice.getPort());
				final DeviceAsync deviceAsync = createDeviceAsync(device);
				deviceList.put(key, deviceAsync);
			}
			
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			System.exit(-1);
		}
		
		if(metaDaten){
			try{
				/* starten des MetaDataService in einem eigenen Thread */
				new MetaDataThread(new File(configPath), new File(sensorsPath), collectorList).start();
			}catch(final NullPointerException es){
				log.info("a config-file was not found, the Server will start without MetaDataService ");
				log.error(es.getMessage(),es);
			}
		}
		
	}
	
	/**
	 * Read the device.xml and convert the Elements into JAXB-Objects
	 * @param path the Path of the config-file (devices.xml)
	 * @return a List with the Elements from the devices.xml
	 * @throws JAXBException Error while reading and converting the devices.xml
	 */
	@SuppressWarnings("unchecked")
	private JaxbDeviceList readDevices(final String path) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        final Unmarshaller um = context.createUnmarshaller();
        final JAXBElement<JaxbDeviceList> tmp = (JAXBElement<JaxbDeviceList>) um.unmarshal(new File(path));
		
		return tmp.getValue();
	}
	
	/* Example: ConnectionType = de.uniluebeck.itm.devicedriver.mockdevice.MockConnection */
	/**
	 * creates a connection instance of the type specified in the devices.xml .
	 * @param connectionType the Type of the Connection
	 * @return Connection for the Device
	 */
	private Connection createConnection(final String connectionType) {
		
		Connection connection = null;
		Class<?> con;
		
		try {
			/* erstellen der richtigen Connection mittels Reflection */
			con = Class.forName(connectionType);
			connection = (Connection) con.newInstance();
		} catch (final Exception e) {
			log.error(e.getMessage());
		}
		return  connection;
	}
	
	/* Example: DeviceName = de.uniluebeck.itm.devicedriver.mockdevice.MockDevice */
	/* Example: DeviceName = de.uniluebeck.itm.devicedriver.jennic.JennicDevice */
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
			/* erstellen des richtigen Devices mittels Reflection */
			deviceClass = Class.forName(deviceName);
			
			/* finden des richtigen Konstruktors */
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
	 * @throws EmptyIdException if thrown when the id-tag in the devices.xml is empty
	 * @throws DuplacateIdException thrown when the id's in the devices.xml are not unique
	 */
	private String createID(final JaxbDevice jaxDevice) throws DuplacateIdException, EmptyIdException{
		
		/* Bei doppelten Id's wird eine DuplacateIdException geworfen */
		if(!jaxDevice.getDeviceId().equalsIgnoreCase("")){
			if(deviceList.containsKey(jaxDevice.getDeviceId())){
				throw new DuplacateIdException(jaxDevice.getDeviceId());
			}else{
				return jaxDevice.getDeviceId();
			}
		}else{
			throw new EmptyIdException();
		}
	}
	/**
	 * get the List with the Devices
	 * @return List with the Devices
	 */
	public Map<String, DeviceAsync> getDeviceList() {
		return deviceList;
	}
}
