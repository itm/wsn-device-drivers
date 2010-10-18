package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * This class describes a capability entity for sensorML format.
 */

@Root
public class Capabilities {

    /**
     * The name of the capability.
     */
    @Attribute
    private String name;

    /**
     * The Data Record Element of the entity.
     */
    @Element
    private sweF10DataRecord sweF10DataRecord;

    /**
     * Returns the name of this entity.
     *
     * @return String name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructor Function.
     *
     * @param name
     * @param data
     */

    public Capabilities(final String name, final sweF10DataRecord data) {
        setName(name);
        setDataRecord(data);

    }


    /**
     * Sets the name for this entity.
     *
     * @param name the new name.
     */
    public final void setName(final String name) {
        this.name = name;
    }


    /**
     * Returns the Data Record element of this entity.
     *
     * @return Data Record element.
     */
    public final sweF10DataRecord getDataRecord() {
        return this.sweF10DataRecord;
    }


    /**
     * Sets the Data Record Element for this entity.
     *
     * @param dataRecord
     */

    public final void setDataRecord(final sweF10DataRecord dataRecord) {
        this.sweF10DataRecord = dataRecord;
    }

}
