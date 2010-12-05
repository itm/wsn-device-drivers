package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * This class describes a Link entity, required in a wiseml file.
 */


public class Link implements Key {

    private int id;

    /**
     * The edge's source.
     */

    @Attribute
    private String source;

    /**
     * The edge's target.
     */

    @Attribute
    private String target;


    @Element
    private String encrypted;

    @Element
    private String virtual;

    @Element
    private Rssi rssi;


    /**
     * List with the capability elements of the entity.
     */

    @ElementList(inline = true)
    private List<Capability> capabilityList;


    /**
     * Requires function for deserializing objects.
     */
    public Link() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param source
     * @param target
     * @param encrypted
     * @param virtual
     * @param rssi
     * @param capList
     */
    public Link(String source, String target, String encrypted, String virtual, Rssi rssi, List capList) {
        setSource(source);
        setTarget(target);
        setEncrypted(encrypted);
        setVirtual(virtual);
        setRssi(rssi);
        setCapabilityList(capList);
    }

    /**
     * Returns the identity of this entity.
     *
     * @return the identity.
     */
    public final int getID() {
        return this.id;
    }


    /**
     * Sets the id for this entity.
     *
     * @param newId the new name.
     */
    public final void setID(final int newId) {
        this.id = newId;
    }

    /**
     * Returns the source node of this entity.
     *
     * @return the source.
     */
    public final String getSource() {
        return this.source;
    }


    /**
     * Sets the source node  for this entity.
     *
     * @param newSource the new name.
     */
    public final void setSource(final String newSource) {
        this.source = newSource;
    }

    /**
     * Returns the target node of this entity.
     *
     * @return the target.
     */
    public final String getTarget() {
        return this.target;
    }


    /**
     * Sets the target node for this entity.
     *
     * @param newTarget the new name.
     */
    public final void setTarget(final String newTarget) {
        this.target = newTarget;
    }


    /**
     * Get the encrypted element.
     *
     * @return String.
     */
    public String getEncrypted() {
        return this.encrypted;
    }

    /**
     * Set the encrypted entity.
     *
     * @param encrypted
     */
    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }


    /**
     * Get the virtual element.
     *
     * @return String.
     */
    public String getVirtual() {
        return this.virtual;
    }

    /**
     * Set the virtual entity.
     *
     * @param virtual
     */
    public void setVirtual(String virtual) {
        this.virtual = virtual;
    }

    /**
     * Gets the rssi element.
     *
     * @return
     */
    public Rssi getRssi() {
        return this.rssi;
    }

    /**
     * Sets the rssi element.
     *
     * @param rssi
     */
    public void setRssi(Rssi rssi) {
        this.rssi = rssi;
    }

    /**
     * Returns a List of capabilities.
     *
     * @return
     */
    public List getCapabilityList() {
        return this.capabilityList;
    }

    /**
     * Set the list of capabilities.
     *
     * @param capList
     */
    public void setCapabilityList(List capList) {
        this.capabilityList = capList;
    }

    /**
     * impements the getKey function from Interface Key.
     *
     * @return the Object(integer) Key of the entity Node.
     */
    public final Object getKey() {
        return getID();
    }

}
