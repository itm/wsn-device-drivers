package model;

import javax.persistence.*;

/**
 * The Timeinfo Entity is described with hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "timeinfo")
public class TimeinfoEntity {

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
     * The  unit field.
     */
    private String unit;

    @Basic
    @Column(name = "unit", nullable = false, length = 100)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * The  end field.
     */
    private String end;

    @Basic
    @Column(name = "end", nullable = false, length = 100)
    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * The  start field.
     */
    private String start;

    @Basic
    @Column(name = "start", nullable = false, length = 100)
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

}

