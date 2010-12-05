package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;


public class EdgeAttribute implements Key {
    /**
     * The identity of this entity.
     */
    @Attribute
    private int id;

    /**
     * The edge identity of this entity.
     */
    @Attribute
    private String edgeId;
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
     * Returns the identity of this entity.
     *
     * @return the identity.
     */
    public final int getID() {
        return this.id;
    }


    /**
     * Sets the id for this entity.
     *
     * @param newId the new name.
     */
    public final void setID(final int newId) {
        this.id = newId;
    }

    /**
     * Returns the edge id of this entity.
     *
     * @return the node identity.
     */
    public final String getEdgeID() {
        return this.edgeId;
    }


    /**
     * Sets the edge id for this entity.
     *
     * @param newEdgeId the new name.
     */
    public final void setEdgeID(final String newEdgeId) {
        this.edgeId = newEdgeId;
    }

    /**
     * Returns the name of this entity.
     *
     * @return the name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Sets the name for this entity.
     *
     * @param newName the new name.
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Returns the value of this entity.
     *
     * @return the value.
     */
    public final String getValue() {
        return this.value;
    }

    /**
     * Sets the Value for this entity.
     *
     * @param newValue the new Os.
     */
    public final void setValue(final String newValue) {
        this.value = newValue;
    }

    /**
     * Constructor.
     *
     * @param id
     * @param edgeID
     * @param name
     * @param value
     */
    public EdgeAttribute(final int id, final String edgeID,
                         final String name, final String value) {
        this.id = id;
        setID(id);
        setEdgeID(edgeID);
        setName(name);
        setValue(value);
    }

    public EdgeAttribute() {

    }

    /**
     * impements the getKey function from Interface Key.
     *
     * @return the Object(integer) Key of the entity EdgeAttribute.
     */
    public final Object getKey() {
        return getID();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
