package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * This class described a Data Record Entity for SensorMl format.
 */
public class sweF10DataRecord {

    /**
     * the definition of the entity .
     */

    @Attribute
    private String definition;

    /**
     * The description of the entity.
     */
    @Element
    private String gmlF10description;


    /**
     * List with the field elements of the entity.
     */
    @ElementList(inline = true)
    private List<sweF10field> sweF10fieldList;


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
     * Returns the description of this entity.
     *
     * @return String description.
     */
    public final String getDescription() {
        return this.gmlF10description;
    }

    /**
     * Sets the description for this entity.
     *
     * @param description the new name.
     */
    public final void setDescription(final String description) {
        this.gmlF10description = description;
    }


    /**
     * This function returns a list with the field elements of this entity.
     *
     * @return the field List
     */
    public final List getFieldList() {
        return sweF10fieldList;
    }

    /**
     * Set the FieldList.
     *
     * @param fieldList
     */
    public final void setFieldList(final List fieldList) {
        this.sweF10fieldList = fieldList;
    }

}
