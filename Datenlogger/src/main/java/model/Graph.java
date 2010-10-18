package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * A Graph Entity is described with Hibernate.
 */

public class Graph {

    @Attribute
    private String gid;

    @Attribute
    private String edgedefault;
    /**
     * List with the node elements of the entity.
     */
    @ElementList(inline = true)
    private List<Node> nodeList;
    /**
     * List with the edge elements of the entity.
     */
    @ElementList(inline = true)
    private List<Edge> edgeList;

    /**
     * @return nodeList
     */
    public final List getNodeList() {
        return nodeList;
    }

    /**
     * @return edgeList
     */
    public final List getEdgeList() {
        return edgeList;
    }


    /**
     * Constructor.
     *
     * @param gID
     * @param edgeDefault
     * @param nList
     * @param eList
     */
    public Graph(final String gID, final String edgeDefault,
                 final List nList, final List eList) {
        gid = gID;
        edgedefault = edgeDefault;
        nodeList = nList;
        edgeList = eList;

    }

    /**
     * GId gid.
     */
    public final String getGId() {
        return gid;
    }

    public final String getEdgeDirection() {
        return edgedefault;
    }

    public Graph() {

    }
}
