package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.metadatenservice.config.Node;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;

public class MetaDataCollector implements IMetaDataCollector {
	private static Logger log = LoggerFactory.getLogger(MetaDataCollector.class);
	private DeviceAsync device = null;
	private String knotenId = "";

	public MetaDataCollector() {
	};

	public MetaDataCollector(DeviceAsync device, String knotenId) {
		this.device = device;
		this.knotenId = knotenId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.metadatacollector.IMetaDataCollector#collect(de.uniluebeck
	 * .itm.devicedriver.Device, java.lang.String)
	 */
	@Override
	public Node collect(File sensorFile) {
		Node node = new Node();
		node.setNodeid(knotenId);
		try {
			InetAddress address = InetAddress.getLocalHost();
			node.setIpAddress(address.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			log.error("Ip-Adresse des TCP-Servers konnte nicht ermittelt werden");
			log.error(e.getMessage());
		}
		node = new FileCollector().filecollect(node, sensorFile);
		node = new DeviceCollector().deviceCollect(device, node);

		return node;
	}

}
