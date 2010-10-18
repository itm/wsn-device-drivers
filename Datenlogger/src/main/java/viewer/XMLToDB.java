package viewer;

import controller.EdgeStore;
import model.Edge;


public class XMLToDB {
    public void fromXMLToDB() {
        final ParseXML parse = new ParseXML();
        parse.readFile("home/kleopatra/Documents/xml/Entity.xml");

        for (int i = 0; i < EdgeStore.getInstance().size(); i++) {

            final StoreToDatabase storeDb = new StoreToDatabase();
          //  final Edge edge = EdgeStore.getInstance().list().get(i);
          //  final String edgeId = edge.getID();
          //  System.out.println(edgeId);
          //  storeDb.retrieveAndStoreEdge(edgeId);
        }
    }
}
