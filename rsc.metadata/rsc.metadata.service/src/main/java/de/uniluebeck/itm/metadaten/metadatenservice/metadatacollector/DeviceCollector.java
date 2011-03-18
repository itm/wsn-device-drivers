package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.metadatenservice.config.Node;

/**
 * Collects all Data that is delvired by the devices themself
 * 
 * @author Toralf Babel
 * 
 */
public class DeviceCollector {
	/** Logger */
	private static Logger log = LoggerFactory.getLogger(DeviceCollector.class);

	/** Constructor */
	public DeviceCollector() {
	};

	/**
	 * Delivers a node. At the moment only the Chiptype can be resolved by the
	 * Devicecollector
	 * 
	 * @param device
	 *            - Device from which we need the MetaData
	 * @param node
	 *            node whith the current known information
	 * @return node with the chiptype as delivered by the device
	 */
	public Node deviceCollect(DeviceAsync device, Node node) {
		//Turned off because this operation kicks other running ops
		//TODO device Listenes who informs Metadatacollector if anything changed
//		ChipType chip = null;
//		try {
//			chip = device.getChipType(10000, new AsyncAdapter<ChipType>())
//					.get();
//		} catch (final Exception e) {
//			log.error(e.getCause().toString());
//		}
//		try {
//			if (!(chip.getName() == null)) {
//				node.setMicrocontroller(chip.getName());
//			}
//		} catch (final NullPointerException e) {
//			// TODO Auto-generated catch block
//			log.error(e.getStackTrace());
//		}
		return node;

	}
}
