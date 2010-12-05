package model;

import javax.persistence.*;

/**
 * A Point of Interest Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "graph", name = "points_of_interest")
public class Points_of_interestEntity {

    private int id;

    @Id
    @Column(name = "id", nullable = false, length = 100)
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }


    private String nodeId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Basic
    @Column(name = "nodeId", nullable = false, length = 100)
    public String getNodeid() {
        return nodeId;
    }

    public void setNodeid(final String nodeid) {
        this.nodeId = nodeid;
    }

    private double x;

    @Basic
    @Column(name = "x", nullable = false)
    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    private double y;

    @Basic
    @Column(name = "y", nullable = false)
    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    private String description;

    @Basic
    @Column(name = "Description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }


}
