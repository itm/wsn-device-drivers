package de.uniluebeck.itm.metadaten.remote.metadataclienthelper;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Capabilities;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.remote.entity.Capability;
import de.uniluebeck.itm.metadaten.remote.entity.Node;
/**
 * 
 * @author babel
 * Delivers some methods for transforming transport objects to local objects
 *
 */
public class NodeHelper {

	/**
	 * Wandelt WiseMlNode in NODE-Message zur Uebertragung per RPC um
	 * 
	 * @param node Node the should be transformed
	 * @return NODE Node for transport with protocolBuffers
	 */
	public NODE changetoNODE(final Node node) {
		final NODE.Builder nodebuilder = NODE.newBuilder();
		if (!(node.getId()== null)) {
			nodebuilder.setKnotenid(node.getId());
		}
		if (!(node.getIpAddress() == null)) {
			nodebuilder.setIp(node.getIpAddress());
		}
		if (!(node.getDescription() == null)) {
			nodebuilder.setDescription(node.getDescription());
		}
		;
		if (!(node.getMicrocontroller() == null)) {
			nodebuilder.setMicrocontroller(node.getMicrocontroller());
		}
		;
		if (!node.getCapabilityList().isEmpty()) {
			for (final Capability cap : node.getCapabilityList()) {
				final Capabilities.Builder capbuilder = Capabilities.newBuilder();
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
	 * Transforms NODE-Message for RPC-transmission to Local node
	 * 
	 * @param nodein rpc- Node for transport
	 * @return Returns a Local node
	 */
	public Node changeToNode(final NODE nodein) {

		final Node nodeout = new Node();
		final List<Capability> capResultList = new ArrayList<Capability>();
		nodeout.setId(nodein.getKnotenid());
		nodeout.setIpAddress(nodein.getIp());
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

}
