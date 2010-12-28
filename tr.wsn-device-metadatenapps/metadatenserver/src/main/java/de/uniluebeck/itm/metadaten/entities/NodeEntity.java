package de.uniluebeck.itm.metadaten.entities;

import javax.persistence.*;

/**
 * A Node Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "metadaten_db", name = "parentnode")
public class NodeEntity {

    /**
     * The Node Id.
     */
    private String id;

    @Id
    @Column(name = "id", nullable = false, length = 100)
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * The  description field.
     */
    private String description;

    @Basic
    @Column(name = "description", nullable = true, length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * The  Microcontroller
     */
    private String microcontroller;

    @Basic
    @Column(name = "microcontroller", nullable = true, length = 100)
    public String getmicrocontroller() {
        return microcontroller;
    }

    public void setmicrocontroller(String microcontroller) {
        this.microcontroller = microcontroller;
    }
    

    /**
     * The  IPAdress
     */
    private String ipAdress;

    @Basic
    @Column(name = "ipAdress", nullable = true, length = 100)
    public String getipAdress() {
        return ipAdress;
    }

    public void setipAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }
    
   
  
}
