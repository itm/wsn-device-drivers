package de.uniluebeck.itm.metadaten.entities;




import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;


public class NodeCapability implements Key {

    /**
     * The identity of this entity.
     */
    @Attribute
    private int id;

    /**
     * The parentnode identity of this entity.
     */
    @Attribute
    private String nodeId;
    /**
     * the name of this entity.
     */
    @Element
    private String name;
    /**
     * The value of this entity.
     */
    @Element
    private String value;

    /**
     * Constructor function.
     *
     * @param id
     * @param nodeId
     * @param name
     * @param value
     */
    public NodeCapability(final int id, final String nodeId, final String name, final String value) {
        setID(id);
        setNodeID(nodeId);
        setName(name);
        setValue(value);
    }

    public NodeCapability() {

    }

    /**
     * Returns the identity of this entity.
     *
     * @return the identity.
     */
    public int getID() {
        return this.id;
    }


    /**
     * Sets the id for this entity.
     *
     * @param newId the new name.
     */
    public void setID(final int newId) {
        this.id = newId;
    }

    /**
     * Returns the parentnode id of this entity.
     *
     * @return the parentnode identity.
     */
    public String getNodeID() {
        return this.nodeId;
    }


    /**
     * Sets the parentnode id for this entity.
     *
     * @param newNodeId the new name.
     */
    public void setNodeID(final String newNodeId) {
        this.nodeId = newNodeId;
    }

    /**
     * Returns the name of this entity.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name for this entity.
     *
     * @param newName the new name.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Returns the value of this entity.
     *
     * @return the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the Value for this entity.
     *
     * @param newValue the new Os.
     */
    public void setValue(final String newValue) {
        this.value = newValue;
    }

    /**
     * impements the getKey function from Interface Key.
     *
     * @return the Object(integer) Key of the entity NodeCapability.
     */
    public Object getKey() {
        return getID();
    }

}
