package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class defines the node defaults section of a wiseml file.
 */

public class NodeDefaults {

    @Attribute
    private String For;

    @Element
    private Node node;

    /**
     * Requires function for deserializing objects.
     */
    public NodeDefaults() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param forAttr
     * @param node
     */

    public NodeDefaults(String forAttr, Node node) {
        this.For = forAttr;
        this.node = node;
    }

    /**
     * Set For attribute.
     *
     * @param For
     */
    public void setFor(String For) {
        this.For = For;
    }

    /**
     * Get For attribute.
     *
     * @return
     */
    public String getFor() {
        return this.For;
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
