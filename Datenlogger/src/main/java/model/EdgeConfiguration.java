package model;

import org.simpleframework.xml.Element;

/**
 * The edge configuration url .
 */

public class EdgeConfiguration {
    /**
     * so as to get a generated strategy.
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
