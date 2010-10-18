package model;

import javax.persistence.*;

/**
 * Describing an Origin Entity with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "origin")
public class OriginEntity {

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
     * The position identity element.
     */

    private int positionid;

    @Id
    @Column(name = "positionid", nullable = false, length = 100)
    public int getPosId() {
        return positionid;
    }

    public void setPosId(final int identity) {

        this.positionid = identity;
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
     * The phi element.
     */
    private double phi;

    @Basic
    @Column(name = "phi", nullable = false, length = 100)
    public double getPhi() {
        return phi;
    }

    public void setPhi(final double phi) {
        this.phi = phi;
    }

    /**
     * The theta element.
     */
    private double theta;

    @Basic
    @Column(name = "theta", nullable = false, length = 100)
    public double getTheta() {
        return theta;
    }

    public void setTheta(final double theta) {
        this.theta = theta;
    }

}
