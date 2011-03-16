package de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadatenservice.config.Capability;
import de.uniluebeck.itm.metadatenservice.config.Node;

public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Uebertragung per RPC um
	 * 
	 * @param node
	 * @return NODE
	 */
	public NODE changetoNODE(Node node) {
		NODE.Builder nodebuilder = NODE.newBuilder();
		if (!(node.getNodeid() == null)) {
			nodebuilder.setKnotenid(node.getNodeid());
		}
		if (!(node.getIpAddress() == null)) {
			nodebuilder.setIp(node.getIpAddress());
		}
		if (!(node.getDescription() == null)) {
			nodebuilder.setDescription(node.getDescription());
		}
		if (!(node.getMicrocontroller() == null)) {
			nodebuilder.setMicrocontroller(node.getMicrocontroller());
		}
		if (!node.getCapability().isEmpty()) {
			for (Capability cap : node.getCapability()) {
				Capabilities.Builder capbuilder = Capabilities.newBuilder();
				capbuilder.setDefaults(cap.getDefault().intValue());
				capbuilder.setName(cap.getName());
				capbuilder.setUnit(cap.getUnit());
				capbuilder.setDatatype(cap.getDatatype());
				capbuilder.setParentnodeId(node.getNodeid());
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
		nodeout.setNodeid(nodein.getKnotenid());
		nodeout.setIpAddress(nodein.getIp());
		nodeout.setMicrocontroller(nodein.getMicrocontroller());
		nodeout.setDescription(nodein.getDescription());
		for (int i = 0; i < nodein.getCapabilityListCount(); i++) {
			Capabilities capItem = nodein.getCapabilityList(i);
			Capability cap = new Capability();
			cap.setDefault(BigInteger.valueOf(capItem.getDefaults()));
			cap.setDatatype(capItem.getDatatype());
			cap.setName(capItem.getName());
			cap.setUnit(capItem.getUnit());
			// cap.setNode(nodeout);
			nodeout.getCapability().add(cap);
		}
		return nodeout;
	}

}
