package de.uniluebeck.itm.metadaten.server.helper;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;

public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Übertragung per RPC um
	 * 
	 * @param node
	 * @return NODE
	 */
	public NODE changetoNODE(Node node) {
		NODE.Builder nodebuilder = NODE.newBuilder();
		if (!(node.getId().isEmpty())) {
			nodebuilder.setKnotenid(node.getId());
		}
		if (!node.getId().isEmpty()) {
			nodebuilder.setKnotenid(node.getId());
		}
		if (!(node.getIpAddress() == null)) {
			nodebuilder.setIp(node.getIpAddress());
		}
		if (!(node.getDescription()==null)) {
			nodebuilder.setDescription(node.getDescription());
		}
		;
		if (!(node.getMicrocontroller()== null)) {
			nodebuilder.setMicrocontroller(node.getMicrocontroller());
		}
		;
		if (!node.getCapabilityList().isEmpty()) {
			for (Capability cap : node.getCapabilityList()) {
				Capabilities.Builder capbuilder = Capabilities.newBuilder();
				capbuilder.setDefaults(cap.getCapDefault());
				capbuilder.setName(cap.getName());
				capbuilder.setUnit(cap.getUnit());
				capbuilder.setDatatype(cap.getDatatype());
				capbuilder.setParentnodeId(node.getId());
				nodebuilder.addCapabilityList(capbuilder.build());
			}
		
		}

		return nodebuilder.build();
	}

	/**
	 * Transforms NODE-Message for RPC to WiseMlNode
	 * 
	 * @param nodein
	 * @return
	 */
	public Node changeToNode(NODE nodein) {

		Node nodeout = new Node();
		List<Capability> capResultList = new ArrayList<Capability>();
		nodeout.setId(nodein.getKnotenid());
		nodeout.setIpAddress(nodein.getIp());
//		
		nodeout.setMicrocontroller(nodein.getMicrocontroller());
		nodeout.setDescription(nodein.getDescription());
		for (int i = 0; i < nodein.getCapabilityListCount(); i++) {
			Capabilities capItem = nodein.getCapabilityList(i);
			Capability cap = new Capability();
			cap.setCapDefault(capItem.getDefaults());
			cap.setDatatype(capItem.getDatatype());
			cap.setName(capItem.getName());
			cap.setUnit(capItem.getUnit());
			cap.setNode(nodeout);
			capResultList.add(cap);
		}
		nodeout.setCapabilityList(capResultList);

		return nodeout;
	}

}
