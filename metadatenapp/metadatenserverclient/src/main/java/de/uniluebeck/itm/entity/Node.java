package de.uniluebeck.itm.entity;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * A Node Entity is described.
 */
public class Node implements Key {

    @Attribute
    private String id="";


    /**
     * Node Defaults Elements.
     */

    @Element
    private String microcontroller="";

    @Element
    private String ipAddress="";

    @Element
    private String description="";


    /**
     * List with the capability elements of the entity.
     */
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
     *
     * @param microcontoller
     * @param ipAddress
     * @param softwareRevision
     * @param otapVersion
     * @param description
     * @param capList
     */
    public Node(String identity, String microcontoller, String IpAddress,
                String description, List capList) {
        setId(identity);
        setMicrocontroller(microcontoller);
        setIpAddress(IpAddress);

        setDescription(description);
        setCapabilityList(capList);
    }


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getMicrocontroller() {
		return microcontroller;
	}


	public void setMicrocontroller(String microcontroller) {
		this.microcontroller = microcontroller;
	}


	public String getApplikations_ID() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public List<Capability> getCapabilityList() {
		return capabilityList;
	}


	public void setCapabilityList(List<Capability> capabilityList) {
		this.capabilityList = capabilityList;
	}


	@Override
	public Object getKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
