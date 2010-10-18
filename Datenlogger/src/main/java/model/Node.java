package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * A Node Entity is described.
 */
public class Node implements Key {

    @Attribute
    private String id;


    /**
     * Node Defaults Elements.
     */

    @Element
    private Position position;

    @Element
    private String gateway;

    @Element
    private String image;

    @Element
    private String nodetype;

    @Element
    private String description;

    @Element
    private Data data;


    /**
     * List with the capability elements of the entity.
     */
    @ElementList(inline = true)
    private List<Capability> capabilityList;


    /**
     * Requires function for deserializing objects.
     */
    public Node() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param identity
     * @param position
     * @param gateway
     * @param image
     * @param nodetype
     * @param description
     * @param capList
     * @param data
     */
    public Node(String identity, Position position, String gateway, String image,
                String nodetype, String description, List capList, Data data) {
        setID(identity);
        setPosition(position);
        setGW(gateway);
        setImage(image);
        setNodeType(nodetype);
        setDescription(description);
        setCapabilityList(capList);
        setData(data);
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
     * Get the position entity.
     *
     * @return Position element.
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Set the position entity.
     *
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Get the gateway entity.
     *
     * @return String.
     */
    public String getGW() {
        return this.gateway;
    }

    /**
     * Set the gateway entity.
     *
     * @param gateway
     */
    public void setGW(String gateway) {
        this.gateway = gateway;
    }


    /**
     * Get the image entity.
     *
     * @return String.
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Set the image entity.
     *
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get the nodetype entity.
     *
     * @return String.
     */
    public String getNodeType() {
        return this.nodetype;
    }

    /**
     * Set the nodetype entity.
     *
     * @param type
     */
    public void setNodeType(String type) {
        this.nodetype = type;
    }


    /**
     * Get the description entity.
     *
     * @return String.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description entity.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Get the data entity.
     *
     * @return Data data.
     */
    public Data getData() {
        return this.data;
    }

    /**
     * Set the data entity.
     *
     * @param data
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Returns a List of capabilities.
     *
     * @return
     */
    public List getCapabilityList() {
        return this.capabilityList;
    }

    /**
     * Set the list of capabilities.
     *
     * @param capList
     */
    public void setCapabilityList(List capList) {
        this.capabilityList = capList;
    }

    /**
     * impements the getKey function from Interface Key.
     *
     * @return the Object(integer) Key of the entity Node.
     */
    public Object getKey() {
        return getID();
    }
}
