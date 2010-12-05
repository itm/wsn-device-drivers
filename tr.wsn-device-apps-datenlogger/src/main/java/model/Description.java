package model;

import org.simpleframework.xml.Element;

/**
 * The class defines the Description  Elements of a wiseml file.
 */

public class Description {

    @Element
    private String description;

    /**
     * Constructor Method.
     *
     * @param description
     */
    public Description(String description) {
        setDescription(description);
    }

    /**
     * Get The description ELement.
     *
     * @return
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Set The Description Element.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


}
