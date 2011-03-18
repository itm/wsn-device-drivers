package de.uniluebeck.itm.metadaten.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * A Node Entity is described.
 */

@Entity
@Table(catalog = "metadaten_db", name = "node")
public class Node implements Serializable {
	
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = -8016541513558233707L;

	@EmbeddedId
	NodeId id;

	@Basic
	@Column(name = "microcontroller", length = 100)
	private String microcontroller;

	@Basic
	@Column(name = "description", length = 100)
	private String description;

	@Basic
	@Column(name = "stamp")
	private Date timestamp;

	/**
	 * List with the capability elements of the entity.
	 */
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parentnode")
	// @OneToMany(mappedBy = "nodeId")
	private List<Capability> capabilityList;

	/**
	 * Requires function for deserializing objects.
	 */
	public Node() {
		super();
	}


	public NodeId getId() {
		return id;
	}
	
	public void setId(final NodeId id) {
		this.id = id;
	}
	
	public String getMicrocontroller() {
		return microcontroller;
	}

	public void setMicrocontroller(final String microcontroller) {
		this.microcontroller = microcontroller;
	}

//	public void setIpAddress(String ipAddress) {
//		this.ipAddress = ipAddress;
//	}
//
//	public String getIpAddress() {
//		return ipAddress;
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public List<Capability> getCapabilityList() {
//		return capabilityList;
		if (null == capabilityList) {
	        return Collections.EMPTY_LIST;
	    }
	    return Collections.unmodifiableList(capabilityList);
	}

	public void setCapabilityList(final List<Capability> capabilityList) {
		this.capabilityList = capabilityList;
	}
}
