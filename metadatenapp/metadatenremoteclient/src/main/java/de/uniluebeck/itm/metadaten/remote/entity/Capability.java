package de.uniluebeck.itm.metadaten.remote.entity;

import org.simpleframework.xml.Element;

/**
 * This class describes a capability entity, required in wiseml file.
 */
public class Capability {

    @Element
    private String name;

    @Element
    private String datatype;

    @Element
    private String unit;

    @Element
    private int defaults;

    /**
     * Requires function for deserializing objects.
     */
    public Capability() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param name
     * @param dtype
     * @param unit
     * @param defaults
     */
    public Capability(String name, String dtype, String unit, int defaults) {
        setName(name);
        setDatatype(dtype);
        setUnit(unit);
        setCapDefault(defaults);
    }

    /**
     * Get the name entity.
     *
     * @return String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name entity.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the datatype entity.
     *
     * @return
     */
    public String getDatatype() {
        return this.datatype;
    }

    /**
     * Set the datatype entity.
     *
     * @param datatype
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }


    /**
     * Get the unit entity.
     *
     * @return String.
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Set the unit entity.
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get the capability default entity.
     *
     * @return int capabilityDefault.
     */
    public int getCapDefault() {
        return this.defaults;
    }

    /**
     * Set the capability Default entity.
     *
     * @param capDefault
     */
    public void setCapDefault(int capDefault) {
        this.defaults = capDefault;
    }

}
