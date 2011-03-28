package de.uniluebeck.itm.metadaten.metadatenserver;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.entities.NodeId;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;

public class dbtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		// TODO Auto-generated method stub
		final Node node = new Node();
		final NodeId idchen = new NodeId();
		idchen.setId("280120101");
		idchen.setIpAdress("192.168.0.101");
//		node.setId(idchen);
//		node.setMicrocontroller("TelosB");
//		node.setDescription("Solar2002");
//		node.setTimestamp(new Date());
		final Capability cap = new Capability ();
		final Capability cap2 = new Capability ();
//		cap.setDatatype("int");
		cap.setName("urn:wisebed:node:capability:temp");
//		cap.setNode(node);
		cap.setUnit("CeLsius");
//		cap.setId(1);
		List <Capability> capList = new ArrayList <Capability>();
		capList.add(cap);
//		cap2.setDatatype("double");
		cap2.setName("urn:wisebed:node:capability:light");
//		cap2.setUnit("Luchs");
//		cap2.setId(2);
//		cap2.setNode(node);
		capList.add(cap2);
//		node.setCapabilityList(capList);
//		node.setPort((short)1234);
//		node.setTimestamp(new Date());
		node.setCapabilityList(capList);
		StoreToDatabase storeDB = new StoreToDatabase();
		DatabaseToStore fromDB = new DatabaseToStore();
		List <Node> resultlist = new ArrayList<Node>();

        resultlist=fromDB.getNodes(node, false);
        System.out.println("Ergebnis:");
        for (int i=0; i<resultlist.size();i++)
        {
        	System.out.println(i + ". Knoten mit id: " + resultlist.get(i).getId().getId() + "IP" + resultlist.get(i).getId().getIpAdress());
        }
	}

}
