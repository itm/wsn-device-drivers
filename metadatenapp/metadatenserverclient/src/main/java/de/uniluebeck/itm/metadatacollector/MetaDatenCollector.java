package de.uniluebeck.itm.metadatacollector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.entity.Node;

public class MetaDatenCollector {
	public Device device;
	public Node node = new Node();
	
	public MetaDatenCollector () {};
	public MetaDatenCollector (Device device)
	{
		this.device=device;
	}
	  
	public Node collect()
	{
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
