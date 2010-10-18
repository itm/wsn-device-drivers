package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class describes a filed entity for sensorML format.
 */

public class sweF10field {

    /**
     * the name of the field .
     */

    @Attribute
    private String name;


    /**
     * the role of the field .
     */

    @Attribute
    private String xlinkF10arcrole;

    /**
     * Quantity Range Element.
     */

    @Element
    private sweF10QuantityRange sweF10QuantityRange;


    /**
     * Returns the field name.
     *
     * @return String name.
     */
    public final String getName() {
        return this.name;
    }


    /**
     * Sets the filed name.
     *
     * @param name = the new field name.
     */
    public final void setName(final String name) {
        this.name = name;
    }


    /**
     * Returns the field role.
     *
     * @return String role.
     */
    public final String getRole() {
        return this.xlinkF10arcrole;
    }


    /**
     * Sets the filed role.
     *
     * @param role = the new field role.
     */
    public final void setRole(final String role) {
        this.xlinkF10arcrole = role;
    }


    /**
     * Returns the Quantity Range Element.
     *
     * @return the range.
     */
    public final sweF10QuantityRange getRange() {
        return this.sweF10QuantityRange;
    }


    /**
     * Sets the Range Element.
     *
     * @param range = the new field range.
     */
    public final void setRange(final sweF10QuantityRange range) {
        this.sweF10QuantityRange = range;
    }


}
