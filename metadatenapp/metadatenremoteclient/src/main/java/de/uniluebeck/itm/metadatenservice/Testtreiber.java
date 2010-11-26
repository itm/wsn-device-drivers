package de.uniluebeck.itm.metadatenservice;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.entity.Node;

public class Testtreiber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MetaDataClient mclient=null;
		try {
			mclient = new MetaDatenClient("frager","testPassword","localhost", 8080, 1235);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Node node = new Node();
        node.setId("123");
		node.setIpAddress("192.168.8.102");
		node.setMicrocontroller("TelosB");
		node.setDescription("Solar2000");
		String queryString = "123";
		List <Node> nodes = new ArrayList<Node>();
		try {
			nodes=mclient.search(node, queryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Folgende Knoten im Verzeichnis, entsprechen Ihrer Suchanfrage:");
		for (int i=0 ;i < nodes.size(); i++)
		{
			System.out.println("Knoten: " +i+1 +" :"+nodes.get(i).toString());
		}
		

	}

}
