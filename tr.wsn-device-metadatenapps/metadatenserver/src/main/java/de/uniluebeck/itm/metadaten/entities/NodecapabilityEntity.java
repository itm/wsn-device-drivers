package de.uniluebeck.itm.metadaten.entities;

import javax.persistence.*;

/**
 * A NodeCapability Entity is described with Hibernate.
 */
@Entity
@Table(catalog = "metadaten_db", name = "nodecapability")
public class NodecapabilityEntity {
    private int id;

    @Id
//    @Column(name = "id", nullable = false, length = 100)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    private String nodeid;

    @Basic
    @Column(name = "nodeId", nullable = false, length = 100)
    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(final String nodeid) {
        this.nodeid = nodeid;
    }

    private String name;

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private String value;

    @Basic
    @Column(name = "value", nullable = false, length = 100)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodecapabilityEntity that = (NodecapabilityEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (nodeid != null ? !nodeid.equals(that.nodeid) : that.nodeid != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nodeid != null ? nodeid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
