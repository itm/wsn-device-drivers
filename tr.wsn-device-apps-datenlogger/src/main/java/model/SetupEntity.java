package model;

import javax.persistence.*;

/**
 * This class describes the SetUp Entity with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "setup")
public class SetupEntity {

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
     * The  description field.
     */
    private String description;

    @Basic
    @Column(name = "description", nullable = false, length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * The  interpolation field.
     */
    private String interpolation;

    @Basic
    @Column(name = "interpolation", nullable = false, length = 100)
    public String getInterpol() {
        return interpolation;
    }

    public void setInterpol(String interpol) {
        this.interpolation = interpol;
    }

}
