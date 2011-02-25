package de.uniluebeck.itm.metadaten.metadatenserver;

import java.util.ArrayList;
import java.util.Date;
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
		// TODO Auto-generated method stub
//		Node node = new Node();
//		NodeId idchen = new NodeId();
//		idchen.setId("231220107");
//		idchen.setIpAdress("192.168.8.115");
//		node.setId(idchen);
//		node.setMicrocontroller("TelosB");
//		node.setDescription("Solar2002");
//		node.setTimestamp(new Date());
//		Capability cap = new Capability ();
//		Capability cap2 = new Capability ();
//		cap.setDatatype("int");
//		cap.setName("Temperatur");
//		cap.setNode(node);
//		cap.setUnit("Grad Fahre");
////		cap.setId(1);
//		List <Capability> capList = new ArrayList <Capability>();
//		capList.add(cap);
//		cap2.setDatatype("double");
//		cap2.setName("Licht");
//		cap2.setUnit("Luchs");
////		cap2.setId(2);
//		cap2.setNode(node);
//		capList.add(cap2);
//		node.setCapabilityList(capList);
//		node.setPort((short)1234);
//		node.setTimestamp(new Date());
//		StoreToDatabase storeDB = new StoreToDatabase();
//		
//		
//
//        parentnode.setId("urn:wisebed:parentnode:cti:gw2:n4");
//        try {
//			storeDB.storeNode(node);
////        	storeDB.updateNode(node);
////        	storeDB.deleteoldNodes(new Date());
////        	storeDB.deleteNode(node);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        DatabaseToStore getfromDB = new DatabaseToStore();
        
        Node getnode = new Node();
        NodeId id = new NodeId();
        id.setId("231220106");
//       id.setIpAdress("192.168.8.115");
        getnode.setId(id);
//        getnode.setMicrocontroller("TelosB");
//        getnode.setIpAddress("192.168.8.114");
//        getnode.setPort((short) 1234);
//        getnode.setDescription("Solar2003");
//        System.out.println("!!!!Test!!!!");
        List <Node> resultlist = new ArrayList<Node>();
//        getnode = getfromDB.getNodes(parentnode);
        resultlist = getfromDB.getNodes(getnode);
        System.out.println("resultlist" + resultlist.size());
        for (Node mynode : resultlist)
        {
        	System.out.println("Knoten aus DB:" + mynode.getId() + "IP" + mynode.getId().getIpAdress() + "Mic " + mynode.getMicrocontroller());
        }
        resultlist=getfromDB.getNodes(getnode);
        for (int i=0; i<resultlist.size();i++)
        {
        	System.out.println(i + ". Knoten mit id: " + resultlist.get(i).getId());
        }
	}

}
