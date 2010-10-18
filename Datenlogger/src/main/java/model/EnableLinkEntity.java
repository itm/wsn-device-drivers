package model;

import javax.persistence.*;

/**
 * This Class describes an Enable Link Entity with Hibernate.
 */


@Entity
@Table(catalog = "wisemlDb", name = "enableLink")
public class EnableLinkEntity {

    /**
     * The enable link identity.
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
     * The link identity.
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
     * The scenario identity.
     */
    private int scenarioid;

    @Basic
    @Column(name = "scenarioid", nullable = false, length = 100)
    public int getSceneId() {
        return scenarioid;
    }

    public void setSceneId(final int sceneid) {

        this.scenarioid = sceneid;
    }

}
