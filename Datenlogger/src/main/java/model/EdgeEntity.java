package model;

import javax.persistence.*;

/**
 * An Edge Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "graph", name = "edge")
public class EdgeEntity {
    private String id;
    private Object obj;

    /**
     * so as to get a generated strategy.
     *
     * @return Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 100)
    public final String getId() {
        return id;
    }

    public final void setId(final String ident) {
        /**
         * Id id.
         */
        this.id = ident;
    }

    private String source;

    /**
     * Source source.
     * so as to get a generated strategy
     *
     * @return Basic
     */
    @Basic
    @Column(name = "source", nullable = false, length = 100)
    public final String getSource() {
        return source;
    }

    public final void setSource(final String source) {
        this.source = source;
    }

    private String target;

    /**
     * so as to get a generated strategy.
     *
     * @return Basic
     */
    @Basic
    @Column(name = "target", nullable = false, length = 100)
    public final String getTarget() {
        return target;
    }

    public final void setTarget(final String target) {
        this.target = target;
    }

    @Override
    public final boolean equals(final Object obj) {
        this.obj = obj;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final EdgeEntity that;
        that = (EdgeEntity) obj;

        if (id != null) {
            if (!id.equals(that.id)) {
                return false;
            }
        } else {
            if (that.id != null) {
                return false;
            }
        }
        if (source != null) {
            if (!source.equals(that.source)) {
                return false;
            }
        } else {
            if (that.source != null) {
                return false;
            }
        }
        if (target != null) {
            if (!target.equals(that.target)) {
                return false;
            }
        } else {
            if (that.target != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final int hashCode() {
        int result;
        if (id != null) {
            result = id.hashCode();
        } else {
            result = 0;
        }
        if (source != null) {
            result = 31 * result + source.hashCode();
        } else {
            result = 31 * result;
        }
        if (target != null) {
            result = 31 * result + target.hashCode();
        } else {
            result = 31 * result;
        }
        return result;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(final Object obj) {
        this.obj = obj;
    }
}
