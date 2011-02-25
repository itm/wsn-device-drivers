package de.uniluebeck.itm.metadaten.server.helper;


import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.entities.NodeId;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;

public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Uebertragung per RPC um
	 * 
	 * @param node
	 * @return NODE
	 */
	public NODE changetoNODE(Node node) {
		NODE.Builder nodebuilder = NODE.newBuilder();
		if (!(node.getId().getId().isEmpty())) {
			nodebuilder.setKnotenid(node.getId().getId());
		}
		if (!(node.getId().getIpAdress() == null)) {
			nodebuilder.setIp(node.getId().getIpAdress());
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
				capbuilder.setParentnodeId(node.getId().getId());
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
		NodeId id = new NodeId();
		List<Capability> capResultList = new ArrayList<Capability>();
		id.setId(nodein.getKnotenid());
		id.setIpAdress(nodein.getIp());
		nodeout.setId(id);
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
	public Node removeEmptyStrings(Node nodein) {
		Node nodeout = new Node();
		NodeId id = new NodeId();
		List <Capability> resultlist = new ArrayList<Capability>();
		if (!(nodein.getId().getId().matches(""))){
			id.setId(nodein.getId().getId());
		}
		if (!(nodein.getId().getIpAdress().matches(""))){
			id.setIpAdress(nodein.getId().getIpAdress());
		}
		if (!(nodein.getDescription().matches(""))) {
			nodeout.setDescription(nodein.getDescription());
		}
		if (!(nodein.getMicrocontroller().matches(""))) {
			nodeout.setMicrocontroller(nodein.getMicrocontroller());
		}
		if ((!(nodein.getPort() == null))) {
			nodeout.setPort(nodein.getPort());
		}
		for (Capability cap : nodein.getCapabilityList()) {
			resultlist.add(removeEmptyStrings(cap));
		}
		nodeout.setCapabilityList(resultlist);
		nodeout.setId(id);
		return nodeout;
	}
	/**
	 * Removes empty strings from the given Capability
	 * @param cap
	 * @return
	 */
	private Capability removeEmptyStrings (Capability cap){
		Capability capout = new Capability();
		if (!(cap.getDatatype().matches(""))) {
			capout.setDatatype(cap.getDatatype());
		}
		if (!(cap.getName().matches(""))) {
			capout.setName(cap.getName());
		}
		if (!(cap.getUnit().matches(""))) {
			capout.setUnit(cap.getUnit());
		}
		return capout;
	}
}
