package viewer;

import controller.EdgeStore;
import controller.NodeStore;
import model.Link;
import model.Node;
import model.Setup;
import model.wiseml;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import java.io.File;
import java.util.List;


public class ParseXML {


    /**
     * Function that reads the given Wiseml File. The Node & Edge entities of the File
     * are read and stored to proper Hashmap models: NodeStore or EdgeStore.
     *
     * @param fileName
     */
    public void readFile(final String fileName) {

        //Read the xml File with a simple xml serializer.
        final Serializer serializer = new Persister();
        final File source = new File(fileName);

        try {

            final wiseml wisemlFile = serializer.read(wiseml.class, source);

            Setup setup = wisemlFile.getSetup();

            /**
             * The list of Nodes that the xml file contains, is scanned and then stored
             * to NodesStore Model.
             */
            final List<Node> nodeList = setup.getNodeList();

            //Each Node is Scanned and Stored to the proper hashMap.

            for (int i = 0; i < nodeList.size(); i++) {
                final Node node = nodeList.get(i);

                //The Node is now stored to the NodeStore HashMap.
                NodeStore.getInstance().add(node);


            }  // Nodes Store.


            final List<Link> edgeList = setup.getLinkList();

            //Each Edge is Scanned and Stored to the proper hashMap.

            for (int i = 0; i < edgeList.size(); i++) {
                final Link edge = edgeList.get(i);
                //The Edge is now stored to the EdgeStore HashMap.
                EdgeStore.getInstance().add(edge);
            }  // Edges Store.

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
