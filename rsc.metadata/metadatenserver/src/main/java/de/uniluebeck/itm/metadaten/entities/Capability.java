package de.uniluebeck.itm.metadaten.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


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

	@Basic
	@Column(name = "name", nullable=true, length = 100)
	private String name;

	@Basic
	@Column(name = "datatype", nullable=true, length = 100)
	private String datatype;

	@Basic
	@Column(name = "unit", nullable=true, length = 100)
	private String unit;

	@Basic
	@Column(name="defaults", nullable=true)
	private Integer defaults;

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
	public Capability(final String name,final  String dtype,final  String unit,final  int defaults) {
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
	public void setName(final String name) {
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
	public void setDatatype(final String datatype) {
		this.datatype = datatype;
	}

	/**
	 * Get the NodeID
	 */
	public Node getNode() {
		return this.parentnode;
	}

	/**
	 * Sets the nodeId to which the Capability belongs
	 * 
	 * @param nodeId
	 */
	public void setNode(final Node node) {
		this.parentnode = node;
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
	public void setUnit(final String unit) {
		this.unit = unit;
	}

	/**
	 * Get the capability default entity.
	 * 
	 * @return int capabilityDefault.
	 */
	public Integer getCapDefault() {
		return this.defaults;
	}

	/**
	 * Set the capability Default entity.
	 * 
	 * @param capDefault
	 */
	public void setCapDefault(final Integer capDefault) {
		this.defaults = capDefault;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
