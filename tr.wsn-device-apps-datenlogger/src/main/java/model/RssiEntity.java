package model;

import javax.persistence.*;

/**
 * This class describes an rssi element with hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "rssi")
public class RssiEntity {

    /**
     * The identity element.
     */

    private int id;

    @Id
    @Column(name = "id", nullable = false, length = 100)
    public int getId() {
        return id;
    }

    public void setId(final int identity) {

        this.id = identity;
    }

    /**
     * The link identity element.
     */
    private int linkid;

    @Basic
    @Column(name = "linkid", nullable = false, length = 100)
    public int getLinkId() {
        return linkid;
    }

    public void setLinkId(final int linkid) {

        this.linkid = linkid;
    }


    /**
     * The  unit field.
     */
    private String unit;

    @Basic
    @Column(name = "unit", nullable = false, length = 100)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    /**
     * The  datatype field.
     */
    private String datatype;

    @Basic
    @Column(name = "datatype", nullable = false, length = 100)
    public String getType() {
        return datatype;
    }

    public void setType(String dtype) {
        this.datatype = dtype;
    }


    /**
     * The  rssi default field.
     */
    private String rssiDefault;

    @Basic
    @Column(name = "rssiDefault", nullable = false, length = 100)
    public String getDefault() {
        return rssiDefault;
    }

    public void setDefault(String rssiDef) {
        this.rssiDefault = rssiDef;
    }

}
