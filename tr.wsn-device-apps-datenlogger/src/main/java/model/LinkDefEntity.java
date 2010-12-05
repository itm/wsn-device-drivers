package model;

import javax.persistence.*;

/**
 * The Link Defaults element described with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "linkDef")
public class LinkDefEntity {


    /**
     * The link  defaults identity element.
     */
    private int id;

    @Id
    @Column(name = "id", nullable = false, length = 100)
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
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
}
