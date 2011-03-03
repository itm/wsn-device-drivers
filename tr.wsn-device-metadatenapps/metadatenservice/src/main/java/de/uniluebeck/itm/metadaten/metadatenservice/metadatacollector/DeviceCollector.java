package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.metadatenservice.MetaDatenService;
import de.uniluebeck.itm.metadatenservice.config.Node;
/**
 * Collects all Data that is delvired by the devices themself
 * @author Toralf Babel
 *
 */
public class DeviceCollector {
	/**Logger*/
	private static Log log = LogFactory.getLog(DeviceCollector.class);
	/** Constructor*/
	public DeviceCollector() {
	};
	/**
	 * Delivers a node. At the moment only the Chiptype can be resolved by the Devicecollector
	 * @param device - Device from which we need the MetaData
	 * @param node node whith the current known information
	 * @return node with the chiptype as delivered by the device
	 */
	public Node devicecollect(Device device, Node node) {
		ChipType chip = null;
		try {
			chip = device.createGetChipTypeOperation().call();
		} catch (final Exception e) {
			log.error(e.getStackTrace());
		}
		try{
			if(!(chip.getName() == null)){
				node.setMicrocontroller(chip.getName());	
			}
		}catch (final NullPointerException e) {
			// TODO Auto-generated catch block
			log.error(e.getStackTrace());
		}
		return node;

	}
}
