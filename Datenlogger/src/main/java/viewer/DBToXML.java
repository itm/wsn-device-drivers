package viewer;

import controller.NodeCapabilityStore;
import controller.NodeStore;
import model.Node;
import model.NodeCapability;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

public class DBToXML {

    /**
     * This Function reads from the database and creates a
     * valid XML file with the retrieved information.
     */
    public final void dBToXML() {


        final CreateXML create = new CreateXML();
        final DatabaseToStore data = new DatabaseToStore();
        data.getNodeToStore("n1");
        data.getNodeCapability(1);
        final List<Node> nodeList = new ArrayList<Node>();
        final List<NodeCapability> capabilityList = new ArrayList<NodeCapability>();
        final NodeCapability nodeC;
        nodeC = NodeCapabilityStore.getInstance().get(1);
        out.println(nodeC.getID());

        capabilityList.add(nodeC);

        final Node node = NodeStore.getInstance().get("n1");

        out.println(node.getID());
      //  node.setNodeCapabilityList(capabilityList);
        nodeList.add(node);
      //  create.writeXML("TestCreateXML.xml", nodeList, null);


    }
}
