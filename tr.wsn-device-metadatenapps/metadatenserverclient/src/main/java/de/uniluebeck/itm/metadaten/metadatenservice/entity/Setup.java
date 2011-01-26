package de.uniluebeck.itm.metadaten.metadatenservice.entity;

import java.util.List;



import org.simpleframework.xml.ElementList;

/**
 * This class describes the setup section of a wiseml file.
 */

public class Setup {
	
    /**
     * List with the node elements of the network.
     */
    @ElementList(inline = true)
    private List<Node> nodeList;
    
    /**
     * Returns a List of Nodes.
     *
     * @return nodeList
     */
    public List <Node> getNodeList() {
        return this.nodeList;
    }

    /**
     * Set the list of nodes.
     *
     * @param nodeList
     */
    public void setNodeList(List <Node> nodeList) {
        this.nodeList = nodeList;
    }

}
