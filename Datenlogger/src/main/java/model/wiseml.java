package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class defines the wiseml file format.
 */

@Root
public class wiseml {


    @Attribute
    private String version;

    @Attribute
    private String xmlnss;  //!!xtipaei la8os sto variable xmlns to simple xml framework!!

    @Element
    private Setup setup;

    @Element
    private Scenario scenario;

    @Element
    private Trace trace;

    /**
     * Required function for Object deserialization.
     */
    public wiseml() {
        super();
    }

    /**
     * Constructor Method.
     *
     * @param version
     * @param xmlns
     * @param setup
     * @param scene
     * @param trace
     */
    public wiseml(String version, String xmlns, Setup setup, Scenario scene,
                  Trace trace) {

        this.version = version;
        this.xmlnss = xmlns;
        this.setup = setup;
        this.scenario = scene;
        this.trace = trace;
    }

    /**
     * Set version attribute.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get For attribute.
     *
     * @return
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Set xmlns attribute.
     *
     * @param xmlns
     */
    public void setXmlns(String xmlns) {
        this.xmlnss = xmlns;
    }

    /**
     * Get xmlns attribute.
     *
     * @return
     */
    public String getXmlns() {
        return this.xmlnss;
    }


    /**
     * Returns the Set up information of this entity.
     *
     * @return the Setup.
     */
    public Setup getSetup() {
        return this.setup;
    }


    /**
     * Sets the setup for this entity.
     *
     * @param setup .
     */
    public void setOrigin(final Setup setup) {
        this.setup = setup;
    }


    /**
     * Returns the Scenario information of this entity.
     *
     * @return the Scenario.
     */
    public Scenario getScenario() {
        return this.scenario;
    }


    /**
     * Sets the Scenario element for this entity.
     *
     * @param scenario .
     */
    public void setScenario(final Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Returns the Trace information of this entity.
     *
     * @return the trace.
     */
    public Trace getTrace() {
        return this.trace;
    }


    /**
     * Sets the Trace element for this entity.
     *
     * @param trace .
     */
    public void setTrace(final Trace trace) {
        this.trace = trace;
    }

}
