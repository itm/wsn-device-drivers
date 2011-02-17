package de.uniluebeck.itm.metadaten.remote.entity;




import org.simpleframework.xml.Element;

/**
 * @author babel
 * This class describes a capability entity, required in wiseml file.
 */
//@Entity
//@Table(catalog = "metadaten_db", name = "capability")
public class Capability {

//	@ManyToOne
	Node parentnode;

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Element
	private String name = "";

	@Element
	private String datatype = "";

	@Element
	private String unit = "";

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
	 * @param name
	 * @param dtype
	 * @param unit
	 * @param defaults
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
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the datatype entity.
	 * 
	 * @return datatype of the capability
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
	 * @return returns the corresponding parentnode
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
	public int getCapDefault() {
		return this.defaults;
	}

	/**
	 * Set the capability Default entity.
	 * 
	 * @param capDefault
	 */
	public void setCapDefault(final int capDefault) {
		this.defaults = capDefault;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

}
