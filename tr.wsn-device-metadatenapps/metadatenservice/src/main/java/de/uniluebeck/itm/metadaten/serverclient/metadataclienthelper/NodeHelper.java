package de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper;


import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.metadatenservice.entity.Capability;
import de.uniluebeck.itm.metadaten.metadatenservice.entity.Node;

public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Uebertragung per RPC um
	 * 
	 * @param node
	 * @return NODE
	 */
	public NODE changetoNODE(Node node) {
		NODE.Builder nodebuilder = NODE.newBuilder();
		if (node.getId().isEmpty()) {
			throw new NullPointerException("KnotenID darf nicht null sein");
		}
		if (!node.getId().isEmpty()) {
			nodebuilder.setKnotenid(node.getId());
		}
		if (!node.getIpAddress().isEmpty()) {
			nodebuilder.setIp(node.getIpAddress());
		}
		if (!node.getDescription().isEmpty()) {
			nodebuilder.setDescription(node.getDescription());
		}
		;
		if (!node.getMicrocontroller().isEmpty()) {
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
	 * Wandelt NODE-Message zur Uebertragung per RPC in WiseMlNode um
	 * 
	 * @param nodein
	 * @return
	 */
	public Node changeToNode(NODE nodein) {

		Node nodeout = new Node();
		List<Capability> capResultList = new ArrayList<Capability>();
		nodeout.setId(nodein.getKnotenid());
		nodeout.setIpAddress(nodein.getIp());
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
