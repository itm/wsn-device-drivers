package model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * This class describes the setup section of a wiseml file.
 */

public class Setup {

    @Element
    Origin origin;

    @Element
    Timeinfo timeinfo;

    @Element
    String interpolation;

    @Element
    String description;

    @Element
    NodeDefaults defaults;

    @Element
    LinkDefaults ldefaults;

    /**
     * List with the node elements of the network.
     */
    @ElementList(inline = true)
    private List<Node> nodeList;

    /**
     * List with the edge elements of the network.
     */
    @ElementList(inline = true)
    private List<Link> linkList;


    public Setup() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param origin
     * @param timeinfo
     * @param interpolation
     * @param description
     * @param defaults
     * @param ldefaults
     * @param nodeList
     * @param linkList
     */
    public Setup(Origin origin, Timeinfo timeinfo, String interpolation, String description,
                 NodeDefaults defaults, LinkDefaults ldefaults, List nodeList, List linkList) {
        setOrigin(origin);
        setTime(timeinfo);
        setInterpol(interpolation);
        setDescription(description);
        setNodeDef(defaults);
        setLinkDef(ldefaults);
        setNodeList(nodeList);
        setLinkList(linkList);
    }

    /**
     * Returns the origin of this entity.
     *
     * @return the origin.
     */
    public Origin getOrigin() {
        return this.origin;
    }


    /**
     * Sets the origin for this entity.
     *
     * @param origin .
     */
    public void setOrigin(final Origin origin) {
        this.origin = origin;
    }


    /**
     * Returns the Time Information of this entity.
     *
     * @return the timeinfo.
     */
    public Origin getTime() {
        return this.origin;
    }


    /**
     * Sets the Time Information for this entity.
     *
     * @param timeinfo .
     */
    public void setTime(final Timeinfo timeinfo) {
        this.timeinfo = timeinfo;
    }

    /**
     * Returns the interpolation of this entity.
     *
     * @return the interpolation.
     */
    public String getInterpol() {
        return this.interpolation;
    }


    /**
     * Sets the id for this entity.
     *
     * @param interpolation .
     */
    public void setInterpol(final String interpolation) {
        this.interpolation = interpolation;
    }

    /**
     * Returns the description of this entity.
     *
     * @return the description.
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Sets the description for this entity.
     *
     * @param description .
     */
    public void setDescription(final String description) {
        this.description = description;
    }


    /**
     * Returns the Node default values.
     *
     * @return Node defaults.
     */
    public NodeDefaults getNodeDef() {
        return this.defaults;
    }


    /**
     * Sets the Node defalut values.
     *
     * @param nodeDef .
     */
    public void setNodeDef(final NodeDefaults nodeDef) {
        this.defaults = nodeDef;
    }

    /**
     * Returns the Link default values.
     *
     * @return Link defaults.
     */
    public LinkDefaults getLinkDef() {
        return this.ldefaults;
    }


    /**
     * Sets the Link defalut values.
     *
     * @param linkDef .
     */
    public void setLinkDef(final LinkDefaults linkDef) {
        this.ldefaults = linkDef;
    }

    /**
     * Returns a List of Nodes.
     *
     * @return nodeList
     */
    public List getNodeList() {
        return this.nodeList;
    }

    /**
     * Set the list of nodes.
     *
     * @param nodeList
     */
    public void setNodeList(List nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * Returns a List of Links.
     *
     * @return linkList
     */
    public List getLinkList() {
        return this.linkList;
    }

    /**
     * Set the Link list .
     *
     * @param linkList
     */
    public void setLinkList(List linkList) {
        this.linkList = linkList;
    }


}
