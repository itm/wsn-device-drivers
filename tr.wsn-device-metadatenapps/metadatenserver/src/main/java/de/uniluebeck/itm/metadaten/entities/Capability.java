package de.uniluebeck.itm.metadaten.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.simpleframework.xml.Element;

/**
 * This class describes a capability entity, required in wiseml file.
 */
@Entity
@Table(catalog = "metadaten_db", name = "capability")
public class Capability {
	
	@ManyToOne
	Node parentnode;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

    @Element
    private String name;

    @Element
    private String datatype;

    @Element
    private String unit;

    @Element
    private int defaults;
    




    /**
     * Requires function for deserializing objects.
     */
    public Capability() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param name
     * @param dtype
     * @param unit
     * @param defaults
     */
    public Capability(String name, String dtype, String unit, int defaults) {
        setName(name);
        setDatatype(dtype);
        setUnit(unit);
        setCapDefault(defaults);
    }

    /**
     * Get the name entity.
     *
     * @return String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name entity.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the datatype entity.
     *
     * @return
     */
    public String getDatatype() {
        return this.datatype;
    }

    /**
     * Set the datatype entity.
     *
     * @param datatype
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
    
    /**
     * Get the NodeID
     */
    public Node getNode ()
    {
    	return this.parentnode;
    }
    
    /**
     * Sets the nodeId to which the Capability belongs
     * @param nodeId
     */
    public void setNode (Node node)
    {
    	this.parentnode=node;
    }


    /**
     * Get the unit entity.
     *
     * @return String.
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Set the unit entity.
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get the capability default entity.
     *
     * @return int capabilityDefault.
     */
    public int getCapDefault() {
        return this.defaults;
    }

    /**
     * Set the capability Default entity.
     *
     * @param capDefault
     */
    public void setCapDefault(int capDefault) {
        this.defaults = capDefault;
    }


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}

}
