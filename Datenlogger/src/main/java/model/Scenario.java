package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class describes the scenario section of a wiseml file.
 */
public class Scenario {

    @Attribute
    private String id;

    @Element
    private String timestamp;

    @Element
    private EnableNode enablenode;

    @Element
    private DisableNode disablenode;

    @Element
    private EnableLink enablelink;

    @Element
    private DisableLink disablelink;

    @Element
    private Node node;

    public Scenario() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param id
     * @param stamp
     * @param enable
     * @param disable
     * @param enablel
     * @param disablel
     * @param node
     */
    public Scenario(String id, String stamp, EnableNode enable, DisableNode disable, EnableLink enablel,
                    DisableLink disablel, Node node) {
        setID(id);
        setTime(stamp);
        setEnable(enable);
        setDisable(disable);
        setEnableL(enablel);
        setDisableL(disablel);
        setNode(node);
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
     * Returns the enable node information for this entity.
     *
     * @return enablenode
     */
    public final EnableNode getEnable() {
        return this.enablenode;
    }


    /**
     * Sets the enable node information  for this entity.
     *
     * @param enable .
     */
    public final void setEnable(final EnableNode enable) {
        this.enablenode = enable;
    }

    /**
     * Returns the disable node information for this entity.
     *
     * @return disablenode
     */
    public final DisableNode getDisable() {
        return this.disablenode;
    }


    /**
     * Sets the disable node information  for this entity.
     *
     * @param disable .
     */
    public final void setDisable(final DisableNode disable) {
        this.disablenode = disable;
    }


    /**
     * Returns the enable link information for this entity.
     *
     * @return enablelink
     */
    public final EnableLink getEnableL() {
        return this.enablelink;
    }


    /**
     * Sets the enable link information  for this entity.
     *
     * @param enable .
     */
    public final void setEnableL(final EnableLink enable) {
        this.enablelink = enable;
    }

    /**
     * Returns the disable link information for this entity.
     *
     * @return disablelink
     */
    public final DisableLink getDisableL() {
        return this.disablelink;
    }


    /**
     * Sets the disable link information  for this entity.
     *
     * @param disable .
     */
    public final void setDisableL(final DisableLink disable) {
        this.disablelink = disable;
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
