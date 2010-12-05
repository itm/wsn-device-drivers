package model;

import org.simpleframework.xml.Element;


public class CapabilityConfiguration {

    /**
     * The capabilities configuration url .
     */

    @Element
    private String name;


    /**
     * Returns the configuration .
     *
     * @return configuration.
     */

    public final String getConfig() {
        return this.name;
    }

}
