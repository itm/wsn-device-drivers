package model;

import javax.persistence.*;

/**
 * This class describes a Position Entity with hibernate .
 */

@Entity
@Table(catalog = "wisemlDb", name = "position")
public class PositionEntity {

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
     * The node identity element.
     */
    private int nodeid;

    @Basic
    @Column(name = "nodeid", nullable = false, length = 100)
    public int getNodeId() {
        return nodeid;
    }

    public void setNodeId(final int nodeid) {

        this.nodeid = nodeid;
    }

    /**
     * The x element.
     */
    private double x;

    @Basic
    @Column(name = "x", nullable = false, length = 100)
    public double getX() {
        return x;
    }

    public void setX(final double xElement) {
        this.x = xElement;
    }


    /**
     * The y element.
     */
    private double y;

    @Basic
    @Column(name = "y", nullable = false, length = 100)
    public double getY() {
        return y;
    }

    public void setY(final double yElement) {
        this.y = yElement;
    }

    /**
     * The z element.
     */
    private double z;

    @Basic
    @Column(name = "z", nullable = false, length = 100)
    public double getZ() {
        return z;
    }

    public void setZ(final double zeta) {
        this.z = zeta;
    }


}
