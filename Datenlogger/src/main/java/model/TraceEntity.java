package model;

import javax.persistence.*;

/**
 * This class describes a  Trace  Entity with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "trace")
public class TraceEntity {

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
     * The  timestamp field.
     */
    private String timestamp;

    @Basic
    @Column(name = "timestamp", nullable = false, length = 100)
    public String getTime() {
        return timestamp;
    }

    public void setTime(String time) {
        this.timestamp = time;
    }


}
