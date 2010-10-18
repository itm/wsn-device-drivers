package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class describes a unit of measurement entity format.
 */
public class sweF10uom {

    /**
     * the code of the uom .
     */

    @Attribute
    private String code;

    /**
     * The value of this entity.
     */

    @Element
    private String value;


    /**
     * Returns the code of this entity.
     *
     * @return String code.
     */
    public final String getCode() {
        return this.code;
    }


    /**
     * Sets the code for this entity for SensorML.
     *
     * @param code the new name.
     */
    public final void setCode(final String code) {
        this.code = code;
    }

    /**
     * Returns the value of this entity.
     *
     * @return the value identity.
     */
    public final String getValue() {
        return this.value;
    }


    /**
     * Sets the value for this entity.
     *
     * @param value the new name.
     */
    public final void setValue(final String value) {
        this.value = value;
    }


}
