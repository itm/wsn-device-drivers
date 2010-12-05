package model;

import javax.persistence.*;

/**
 * This class describes a Scenario Entity with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "scenario")
public class ScenarioEntity {

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
