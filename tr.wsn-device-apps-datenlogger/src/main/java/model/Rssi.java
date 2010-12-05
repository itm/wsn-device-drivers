package model;

import org.simpleframework.xml.Attribute;

/**
 * This class displays the rssi element, required in a wiseml file.
 */

public class Rssi {

    String rssId;

    @Attribute
    String datatype;

    @Attribute
    String unit;

    @Attribute
    String rssiDefault;

    /**
     * Requires function for deserializing objects.
     */
    public Rssi() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param type
     * @param unit
     * @param rssiDef
     */

    public Rssi(String type, String unit, String rssiDef) {
        setDatatype(type);
        setUnit(unit);
        setDefault(rssiDef);
    }

    /**
     * Get the  rssi identity.
     *
     * @return
     */
    public String GetId() {
        return this.rssId;

    }

    /**
     * Set the rssi Identity.
     *
     * @param rssId
     */
    public void SetId(String rssId) {
        this.rssId = rssId;
    }

    /**
     * Sets datatype attribute.
     *
     * @param datatype
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;

    }


    /**
     * Gets the datatype attribute.
     *
     * @return
     */
    public String getDatatype() {
        return this.datatype;
    }

    /**
     * Sets unit attribute.
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;

    }


    /**
     * Gets the unit attribute.
     *
     * @return
     */
    public String getUnit() {
        return this.unit;
    }


    /**
     * Sets rssi default attribute.
     *
     * @param rssiDef
     */
    public void setDefault(String rssiDef) {
        this.rssiDefault = rssiDef;

    }


    /**
     * Gets the rssi default attribute.
     *
     * @return
     */
    public String getDefault() {
        return this.rssiDefault;
    }

}
