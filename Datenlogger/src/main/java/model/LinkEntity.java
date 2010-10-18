package model;

import javax.persistence.*;

/**
 * Describing a link entity with hibernate .
 */
@Entity
@Table(catalog = "wisemlDb", name = "link")
public class LinkEntity {

    /**
     * The link identity element.
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
     * The encrypted filed.
     */
    private String encrypted;

    @Basic
    @Column(name = "encrypted", nullable = false, length = 100)
    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(final String encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * The link's source element.
     */
    private String source;

    @Basic
    @Column(name = "source", nullable = false, length = 100)
    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * The link's target element.
     */
    private String target;

    @Basic
    @Column(name = "target", nullable = false, length = 100)
    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    /**
     * The virtual fileld.
     */
    private String virtual;

    @Basic
    @Column(name = "virtual", nullable = false, length = 100)
    public String getVirtual() {
        return virtual;
    }

    public void setVirtual(final String virtual) {
        this.virtual = virtual;
    }

}
