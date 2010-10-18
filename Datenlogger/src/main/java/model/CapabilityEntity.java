package model;

import javax.persistence.*;

/**
 * A Capability Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "capability")
public class CapabilityEntity {

    /**
     * The name of the capability element.
     */
    private String name;

    @Id
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The data type filed.
     */
    private String datatype;

    @Basic
    @Column(name = "datatype", nullable = false, length = 100)
    public String getDataType() {
        return datatype;
    }

    public void setDataType(final String type) {

        this.datatype = type;
    }

    /**
     * The unit field of the capability element.
     */
    private String unit;

    @Basic
    @Column(name = "unit", nullable = false, length = 100)
    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {

        this.unit = unit;
    }

    /**
     * The link defaults identity element.
     */
    private int liDefaultid;

    @Basic
    @Column(name = "liDefaultid", nullable = false, length = 100)
    public int getLinkDef() {
        return liDefaultid;
    }

    public void setLinkDef(final int linkDef) {

        this.liDefaultid = linkDef;
    }


    /**
     * The link identity element.
     */
    private int linkid;

    @Basic
    @Column(name = "linkid", nullable = false, length = 100)
    public int getLin() {
        return linkid;
    }

    public void setLink(final int link) {

        this.linkid = link;
    }


    /**
     * The node defaults identity element.
     */
    private int noDefaultid;

    @Basic
    @Column(name = "noDefaultid", nullable = false, length = 100)
    public int getNodefault() {
        return noDefaultid;
    }

    public void setNodefault(final int nodeDef) {

        this.noDefaultid = nodeDef;
    }

    /**
     * the node identity element.
     */
    private int nodeid;

    @Basic
    @Column(name = "nodeid", nullable = false, length = 100)
    public int getNode() {
        return nodeid;
    }

    public void setNode(final int node) {

        this.nodeid = node;
    }

}
