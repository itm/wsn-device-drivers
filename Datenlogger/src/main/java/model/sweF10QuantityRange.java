package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;


/**
 * This class describes a Quantity Range entity for SensorMl format.
 */

public class sweF10QuantityRange {

    /**
     * the definition of the QuantityRange .
     */

    @Attribute
    private String definition;

    /**
     * The value of this entity.
     */

    @Element
    private sweF10uom sweF10uom;

    /**
     * Returns the definition of this entity.
     *
     * @return String definition.
     */
    public final String getDefinition() {
        return this.definition;
    }


    /**
     * Sets the definition for this entity.
     *
     * @param definition the new name.
     */
    public final void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Returns the uom entity.
     *
     * @return the uom .
     */
    public final sweF10uom getUom() {
        return this.sweF10uom;
    }


    /**
     * Sets the uom for this entity.
     *
     * @param sweF10uom the new uom.
     */
    public final void setUom(final sweF10uom sweF10uom) {
        this.sweF10uom = sweF10uom;
    }


}
