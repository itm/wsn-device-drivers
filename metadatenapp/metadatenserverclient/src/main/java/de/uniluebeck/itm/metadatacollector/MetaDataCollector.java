package de.uniluebeck.itm.metadatacollector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.entity.Node;

public class MetaDataCollector implements IMetaDataCollector {
	private Device device = null;
	private String knotenId="";
	
	public MetaDataCollector (){};
	public MetaDataCollector (Device device, String knotenId)
	{
		this.device=device;
		this.knotenId=knotenId;
	}
	  
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.metadatacollector.IMetaDataCollector#collect(de.uniluebeck.itm.devicedriver.Device, java.lang.String)
	 */
	@Override
	public Node collect()
	{
		Node node = new Node();
		node.setId(knotenId);
		try {
			InetAddress address = InetAddress.getLocalHost();
			node.setIpAddress(address.getHostAddress() );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.println("Ip-Adresse des TCP-Servers konnte nicht ermittelt werden");
			e.printStackTrace();
		}
		node = new DeviceCollector ().devicecollect(device, node);
		node = new FileCollector().filecollect(node);
		return node;
	}


}
