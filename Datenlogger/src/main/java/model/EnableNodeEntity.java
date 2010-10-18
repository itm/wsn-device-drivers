package model;

import javax.persistence.*;

/**
 * Describing an Enable Node Entity with Hibernate.
 */

@Entity
@Table(catalog = "wisemlDb", name = "enableNode")
public class EnableNodeEntity {

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
     * The node identity element.
     */
   private int nodeid;

   @Basic
   @Column(name = "nodeid", nullable = false, length = 100)
   public int getNodeId() {
       return nodeid;
   }

   public void setNodeId(final int nodeid) {

       this.nodeid = nodeid;
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
