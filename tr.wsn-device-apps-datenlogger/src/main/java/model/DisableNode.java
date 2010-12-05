package model;

import org.simpleframework.xml.Attribute;

/**
 * This class describes the disableNode entity for the scenario section of a wiseml file.
 */

public class DisableNode {

    @Attribute
    private String id;


    /**
     * Requires function for deserializing objects.
     */
    public DisableNode() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param identity
     */
    public DisableNode(String identity) {
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
