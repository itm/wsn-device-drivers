package de.uniluebeck.itm.metadaten.metadatenservice.metadatacollector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uniluebeck.itm.metadatenservice.config.Node;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;

public class MetaDatenCollector {
	public DeviceAsync device;
	public Node node = new Node();
	
	public MetaDatenCollector () {};
	public MetaDatenCollector (DeviceAsync device)
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
		node = new DeviceCollector ().deviceCollect(device, node);
//		node = new FileCollector().filecollect(node);
		return node;
	}


}
