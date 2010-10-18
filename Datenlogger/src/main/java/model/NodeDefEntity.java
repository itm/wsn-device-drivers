package model;

import javax.persistence.*;

/**
 * Description of a Node Defaults Entity with hibernate.
 */
@Entity
@Table(catalog = "wisemlDb", name = "nodeDef")
public class NodeDefEntity {


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
     * The set up identity element.
     */
    private int setupid;

    @Basic
    @Column(name = "setupid", nullable = false, length = 100)
    public int getSetup() {
        return setupid;
    }

    public void setSetup(final int setid) {
        this.setupid = setid;
    }


}
