package model;

import javax.persistence.*;

/**
 * An edgeAttribute Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "graph", name = "edgeattribute")


public class EdgeattributeEntity {
    /**
     * Edgeattribute id.
     */
    private int id;

    /**
     * so as to set a generated strategy.
     *
     * @return Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 100)
    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    /**
     * Edgeid edgeid.
     */
    private String edgeid;

    /**
     * so as to set a generated strategy.
     *
     * @return Basic
     */
    @Basic
    @Column(name = "edgeId", nullable = false, length = 100)

    public final String getEdgeid() {
        return edgeid;
    }

    /**
     * @param edgeid
     */
    public final void setEdgeid(final String edgeid) {


        this.edgeid = edgeid;
    }

    private String name;

    /**
     * so as to set a generated strategy.
     */
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    private String value;

    /**
     * so as to set a generated strategy.
     *
     * @return Basic
     */
    @Basic
    @Column(name = "value", nullable = false, length = 100)
    public final String getValue() {
        return value;
    }

    public final void setValue(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final EdgeattributeEntity that;
        that = (EdgeattributeEntity) obj;

        if (id != that.id) {
            return false;
        }
        if (edgeid != null) {
            if (!edgeid.equals(that.edgeid)) {
                return false;
            }
        } else {
            if (that.edgeid != null) {
                return false;
            }
        }
        if (name != null) {
            if (!name.equals(that.name)) {
                return false;
            }
        } else {
            if (that.name != null) {
                return false;
            }
        }
        if (value != null) {
            if (!value.equals(that.value)) {
                return false;
            }
        } else {
            if (that.value != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final int hashCode() {
        int result = id;
        if (edgeid != null) {
            result = 31 * result + edgeid.hashCode();
        } else {
            result = 31 * result + 0;
        }
        if (name != null) {
            result = 31 * result + name.hashCode();
        } else {
            result = 31 * result + 0;
        }
        if (value != null) {
            result = (31 * result) + value.hashCode();
        } else {
            result = 31 * result + 0;
        }
        return result;
    }
}
