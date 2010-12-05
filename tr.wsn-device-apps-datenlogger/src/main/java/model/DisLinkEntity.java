package model;

import javax.persistence.*;

/**
 * A Disconnect Link Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "disLink")
public class DisLinkEntity {

    /**
     * The disable link identity element.
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
     * The scenario identity element.
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
