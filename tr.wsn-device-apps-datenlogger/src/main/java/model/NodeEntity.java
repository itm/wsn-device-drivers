package model;

import javax.persistence.*;

/**
 * A Node Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "node")
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
    @Column(name = "description", nullable = false, length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The  gateway element.
     */
    private String gateway;

    @Basic
    @Column(name = "gateway", nullable = false, length = 100)
    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * The  image element.
     */
    private String image;

    @Basic
    @Column(name = "image", nullable = false, length = 100)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * The  node type element.
     */
    private String nodetype;

    @Basic
    @Column(name = "nodetype", nullable = false, length = 100)
    public String getType() {
        return nodetype;
    }

    public void setType(String type) {
        this.nodetype = type;
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
