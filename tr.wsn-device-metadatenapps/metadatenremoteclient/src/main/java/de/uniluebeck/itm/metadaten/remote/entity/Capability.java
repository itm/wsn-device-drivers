package de.uniluebeck.itm.metadaten.remote.entity;




import org.simpleframework.xml.Element;

/**
 * @author babel
 * This class describes a capability entity, required in wiseml file.
 */
//@Entity
//@Table(catalog = "metadaten_db", name = "capability")
public class Capability {

	/**
	 * Node to which the capability belongs to
	 */
//	@ManyToOne
	private Node parentnode;
	/**
	 * id only used for storing in database, is generated by the metadataserver
	 */
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	/**
	 * name of the capability
	 */
	@Element
	private String name = "";
	/**
	 * datatype of the unit of measure
	 */
	@Element
	private String datatype = "";
	/**
	 * unit of measure e.g. celsius
	 */
	@Element
	private String unit = "";
	/**
	 * default value for the unit of measure
	 */
	@Element
	private int defaults = 0;

	/**
	 * Requires function for deserializing objects.
	 */
	public Capability() {
		super();
	}

	/**
	 * Constructor Method.
	 * 
	 * @param name name of the capability
	 * @param dtype datatype of the unit of measure e.g. integer 
	 * @param unit unit of measure e.g. degree celsius
	 * @param defaults default value for unit
	 */
	public Capability(final String name, final String dtype, final String unit, final int defaults) {
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
	 * @param name name of the capability
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the datatype entity.
	 * 
	 * @return datatype of the unit of measure e.g. integer
	 */
	public String getDatatype() {
		return this.datatype;
	}

	/**
	 * Set the datatype entity.
	 * 
	 * @param datatype datatype of the unit of measure e.g. integer 
	 */
	public void setDatatype(final String datatype) {
		this.datatype = datatype;
	}

	/**
	 * Get the NodeID
	 * @return returns the corresponding parentnode
	 */
	public Node getNode() {
		return this.parentnode;
	}

	/**
	 * Sets the nodeId to which the Capability belongs
	 * 
	 * @param node  This the sensor node to which the capability belongs
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
	 * @param unit unit of measure e.g. degree celsius
	 */
	public void setUnit(final String unit) {
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
	 * @param capDefault default value for this unit of measure
	 */
	public void setCapDefault(final int capDefault) {
		this.defaults = capDefault;
	}
	/**
	 * Delivers the node id to which the capability belongs
	 * @return integer value of the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * sets the id, not necessary
	 * @param id
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

}
