package model;

import org.simpleframework.xml.Element;

/**
 * The configuration of the entities that consist our network.
 */

public class Configuration {
    /**
     * The configuration url .
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
