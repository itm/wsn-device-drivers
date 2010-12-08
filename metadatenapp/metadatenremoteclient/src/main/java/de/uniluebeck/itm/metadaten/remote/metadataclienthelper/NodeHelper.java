package de.uniluebeck.itm.metadaten.remote.metadataclienthelper;

import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.remote.entity.Node;

public class NodeHelper {
	
	/**
	 * Wandelt WiseMlNode in NODE-Message zur Übertragung per RPC um
	 * @param node
	 * @return NODE
	 */
	public NODE  changetoNODE(Node node)
	{
		//TODO CapabilityList auswerten
		NODE.Builder nodebuilder = NODE.newBuilder();
		if (node.getId().isEmpty()){ throw new NullPointerException("KnotenID darf nicht null sein");}
		if (!node.getId().isEmpty()){nodebuilder.setKnotenid(node.getId());}
		if (!node.getIpAddress().isEmpty()){nodebuilder.setIp(node.getIpAddress());}
		if (!node.getDescription().isEmpty()){nodebuilder.setDescription(node.getDescription());};
		if (!node.getMicrocontroller().isEmpty()){nodebuilder.setMicrocontroller(node.getMicrocontroller());};
//		if (!node.gnodebuilder.setSensoren(node.getCapabilityList().toString());
		
		return nodebuilder.build();
	}
	
	/**
	 * Wandelt NODE-Message zur Übertragung per RPC  in WiseMlNode um
	 * @param nodein
	 * @return
	 */
	public Node changeToNode(NODE nodein)	
	{

			Node nodeout = new Node();
			nodeout.setId(nodein.getKnotenid());
			nodeout.setIpAddress(nodein.getIp());
			nodeout.setMicrocontroller(nodein.getMicrocontroller());
			nodeout.setDescription(nodein.getDescription());
			//TODO CapabilityList
//			nodeout.setCapabilityList(nodein.getSensoren());
			
			return nodeout;
	}

}
