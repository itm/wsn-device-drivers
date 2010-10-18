package model;

import javax.persistence.*;

/**
 * This Class describes a wiseml file with hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "wmlfiles")
public class WmlfilesEntity {

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
     * The trace identity element.
     */
    private int traceid;

    @Basic
    @Column(name = "traceid", nullable = false, length = 100)
    public int getTrace() {
        return traceid;
    }

    public void setTrace(final int traceid) {
        this.traceid = traceid;
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
