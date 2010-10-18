package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class describes the Link Defaults, required in a wiseml file.
 */

public class LinkDefaults {


    @Attribute
    private String For;

    @Element
    private Link link;

    /**
     * Requires function for deserializing objects.
     */
    public LinkDefaults() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param forAttr
     * @param link
     */
    public LinkDefaults(String forAttr, Link link) {
        this.For = forAttr;
        this.link = link;
    }

    /**
     * Set For attribute.
     *
     * @param For
     */
    public void setFor(String For) {
        this.For = For;
    }

    /**
     * Get For attribute.
     *
     * @return
     */
    public String getFor() {
        return this.For;
    }

    /**
     * Set Link element.
     *
     * @param link
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * Get the Link element.
     *
     * @return
     */

    public Link getLink() {
        return this.link;
    }

}
