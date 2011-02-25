package de.uniluebeck.itm.metadaten.remote.entity;


import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * @author babel
 * A Node Entity is described.
 */

public class Node {

	/**
	 * Id of the node
	 */
	@Attribute
	private String id;

	/**
	 * the microcontroller build on the sensor node
	 */

	@Element(required= false)
	private String microcontroller;
	/**
	 * Ip-Adress of the Tcp-Server to which the node is connected
	 */
	@Element(required= false)
	private String ipAddress;
	/**
	 * Testbed to which the sensor node belongs
	 */
	@Element(required= false)
	private String description;
	/**
	 * port to which the node is connected
	 */
	@Element
	private short port;
	/**
	 * Timestamp on which the node was last refreshed
	 */
	@Element(required= false)
	private Date timestamp;

	/**
	 * List with the capability elements of the entity.
	 */
	// @OneToMany(mappedBy = "nodeId")
	@ElementList(inline = true)
	private List<Capability> capabilityList;

	/**
	 * Requires function for deserializing objects.
	 */
	public Node() {
		super();
	}

	/**
	 * Constructor Method.
	 * @param identity id number of the node
	 * @param microcontoller type of microcontroller of the node
	 * @param IpAddress determines the ipAddress of the server the node is connected to
	 * @param description the testbed to which the node belongs
	 * @param capList List of capabilities the sensor node offers
	 */
	public Node(final String identity, final String microcontoller, final String IpAddress,
			final String description, final List<Capability> capList) {
		setId(identity);
		setMicrocontroller(microcontoller);
		setIpAddress(IpAddress);

		setDescription(description);
		setCapabilityList(capList);
	}
	/**
	 * id of node
	 * @return string id of the node
	 */
	public String getId() {
		return id;
	}
	/**
	 * Set id of the node
	 * @param id id of the node
	 */
	public void setId(final String id) {
		this.id = id;
	}
	/**
	 * retunr microcontroller of the node
	 * @return string name of microcontroller
	 */
	public String getMicrocontroller() {
		return microcontroller;
	}
	/**
	 * Set the name of the mic-controller
	 * @param microcontroller name of the mic-controller
	 */
	public void setMicrocontroller(final String microcontroller) {
		this.microcontroller = microcontroller;
	}
	/**
	 * Set Ip-Address of the Tcp-server
	 * @param ipAddress of the Tcp-server
	 */
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * delivers Ip Adress of the Tcp-server
	 * @return string IpAddress of the Tcp-server
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * delivers name of the testbed
	 * @return String description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * sets the name of the testbed
	 * @param  description name of testbed
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	/**
	 * Port of the node
	 * @param port to which the sensor node is connected
	 */
	public void setPort(final short port) {
		this.port = port;
	}
	/**
	 * delivers port of the node
	 * @return  short port to which the sensor node is connected
	 */
	public short getPort() {
		return this.port;
	}
	/**
	 * set timestamp
	 * @param timestamp time of last refresh
	 */
	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * get the timestamp of last refresh
	 * @return Date the date
	 */
	public Date getTimestamp() {
		return this.timestamp;
	}
	/**
	 * list of capabilites
	 * @return List<Capability> list of capabilties
	 */
	public List<Capability> getCapabilityList() {
//		return capabilityList;
		if (null == capabilityList) {
	        return Collections.emptyList();
	    }
	    return Collections.unmodifiableList(capabilityList);
	}
	/**
	 * Cap-List
	 * @param capabilityList list of the capabilities
	 */
	public void setCapabilityList(final List<Capability> capabilityList) {
		this.capabilityList = capabilityList;
	}

}
