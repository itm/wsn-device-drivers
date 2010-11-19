package de.uniluebeck.itm.metadaten.entities;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * A Node Entity is described.
 */
public class Node implements Key {

    @Attribute
    private String id;


    /**
     * Node Defaults Elements.
     */

    @Element
    private String microcontroller;

    @Element
    private String applikations_ID;

    @Element
    private String softwareRevision;

    @Element
    private String otapVersion;

    @Element
    private String testbed;


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
     * @param applikations_ID
     * @param softwareRevision
     * @param otapVersion
     * @param testbed
     * @param capList
     */
    public Node(String identity, String microcontoller, String applikations_ID, String softwareRevision, String otapVersion,
                String testbed, List capList) {
        setId(identity);
        setMicrocontroller(microcontoller);
        setApplikations_ID(applikations_ID);
        setSoftwareRevision(softwareRevision);
        setOtapVersion(otapVersion);
        setTestbed(testbed);
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
		return applikations_ID;
	}


	public void setApplikations_ID(String applikationsID) {
		applikations_ID = applikationsID;
	}


	public String getSoftwareRevision() {
		return softwareRevision;
	}


	public void setSoftwareRevision(String softwareRevision) {
		this.softwareRevision = softwareRevision;
	}


	public String getOtapVersion() {
		return otapVersion;
	}


	public void setOtapVersion(String otapVersion) {
		this.otapVersion = otapVersion;
	}


	public String getTestbed() {
		return testbed;
	}


	public void setTestbed(String testbed) {
		this.testbed = testbed;
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
