package model;

import org.simpleframework.xml.Attribute;

/**
 * This class describes the Deta element needed in the scenario section of a wiseml file.
 */
public class Data {

    @Attribute
    private String key;


    /**
     * Requires function for deserializing objects.
     */
    public Data() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param key
     */
    public Data(String key) {
        this.key = key;
    }

    /**
     * Returns the key of this entity.
     *
     * @return the identity.
     */
    public String getKey() {
        return this.key;
    }


    /**
     * Sets the key for this entity.
     *
     * @param key the new name.
     */
    public void setID(final String key) {
        this.key = key;
    }


}
