package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class describes the trace section required in a wiseml file.
 */
public class Trace {

    @Attribute
    private String id;

    @Element
    private String timestamp;

    @Element
    private Node node;

    @Element
    private Link link;


    public Trace() {
        super();
    }


    /**
     * Constractor Method.
     *
     * @param identity
     * @param timestamp
     * @param node
     * @param link
     */

    public Trace(String identity, String timestamp, Node node, Link link) {
        setID(identity);
        setTime(timestamp);
        setNode(node);
        setLink(link);
    }


    /**
     * Returns the identity of this entity.
     *
     * @return the identity.
     */
    public String getID() {
        return this.id;
    }


    /**
     * Sets the id for this entity.
     *
     * @param newId the new name.
     */
    public void setID(final String newId) {
        this.id = newId;
    }


    /**
     * Returns the timestamp for this entity.
     *
     * @return the timestamp.
     */
    public final String getTime() {
        return this.timestamp;
    }


    /**
     * Sets the timestamp for this entity.
     *
     * @param stamp .
     */
    public final void setTime(final String stamp) {
        this.timestamp = stamp;
    }

    /**
     * Set Link element.
     *
     * @param link
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * Get the Link element.
     *
     * @return
     */

    public Link getLink() {
        return this.link;
    }

    /**
     * Set Node element.
     *
     * @param node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Get node element.
     *
     * @return
     */

    public Node getNode() {
        return this.node;
    }

}
