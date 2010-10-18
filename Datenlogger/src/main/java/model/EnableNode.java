package model;

import org.simpleframework.xml.Attribute;

/**
 * This class describes the enable node entity for the scenario section of a wiseml file.
 */

public class EnableNode {

    @Attribute
    private String id;

    /**
     * Requires function for deserializing objects.
     */
    public EnableNode() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param identity
     */
    public EnableNode(String identity) {
        setID(identity);
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


}
