package de.uniluebeck.itm.metadaten.remote.client;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.metadaten.remote.entity.Node;


/**
 * 
 * @author tora
 * just a test implementation
 *
 */
public class Testtreiber {

	/**
	 * @param args argumentlist
	 */
	public static void main(final String[] args) {
		// TODO Authentifizierung fuer die Abfrage von Daten?
		final int serverport = 8080;
		final int clientport = 1235;
		MetaDataClient mclient=null;
		try {
			mclient = new MetaDatenClient("frager","testPassword","localhost", serverport, clientport);
		} catch (final Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Node wird gebaut");
		final Node node = new Node();
//        node.setId("111296050613777");
		node.setIpAddress("192.168.8.115");
//		node.setMicrocontroller("mic1");
//		node.setDescription("Solar2000");
		final String queryString = "123";
		List <Node> nodes = new ArrayList<Node>();
		try {
			nodes=mclient.search(node, queryString);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Folgende Knoten im Verzeichnis, entsprechen Ihrer Suchanfrage:" + nodes.size());
		for (int i=0 ;i < nodes.size(); i++)
		{
			System.out.println("Knoten: " +i+1 +" :"+nodes.get(i).getId());
		}
		

	}

}
