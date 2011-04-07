package de.uniluebeck.itm.metadaten.server.helper;


import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.entities.NodeId;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;

/**
 * This class is used for transformation of transportobjects to their local objects and return
 * @author babel
 *
 */
public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Uebertragung per RPC um
	 * 
	 * @param node - local node object
	 * @return NODE - Node object for transport
	 */
	public NODE changetoNODE(final Node node) {
		final NODE.Builder nodebuilder = NODE.newBuilder();
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
				final Capabilities.Builder capbuilder = Capabilities.newBuilder();
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
	 * Transforms NODE-Message for RPC to Local Node
	 * 
	 * @param nodein - node used for transport
	 * @return Node - local node representative
	 */
	public Node changeToNode(final NODE nodein) {

		final Node nodeout = new Node();
		final NodeId id = new NodeId();
		final List<Capability> capResultList = new ArrayList<Capability>();
		id.setId(nodein.getKnotenid());
		id.setIpAdress(nodein.getIp());
		nodeout.setId(id);
		nodeout.setMicrocontroller(nodein.getMicrocontroller());
		nodeout.setDescription(nodein.getDescription());
		for (int i = 0; i < nodein.getCapabilityListCount(); i++) {
			final Capabilities capItem = nodein.getCapabilityList(i);
			final Capability cap = new Capability();
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
	/**
	 * Removes empty strings and replaces them by null
	 * @param nodein - local node object
	 * @return Node - emptystring replaced by null
	 */
	public Node removeEmptyStrings(final Node nodein) {
		final Node nodeout = new Node();
		final NodeId id = new NodeId();
		final List <Capability> resultlist = new ArrayList<Capability>();
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
		for (Capability cap : nodein.getCapabilityList()) {
			resultlist.add(removeEmptyStrings(cap));
		}
		nodeout.setCapabilityList(resultlist);
		nodeout.setId(id);
		return nodeout;
	}
	/**
	 * Removes empty strings from the given Capability
	 * @param cap - local capability
	 * @return Capability - freed from empty strings
	 */
	private Capability removeEmptyStrings (final Capability cap){
		final Capability capout = new Capability();
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
