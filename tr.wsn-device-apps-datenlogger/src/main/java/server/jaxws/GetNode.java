package server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * so as to get a generated strategy.
 */
@XmlRootElement(name = "getNode", namespace = "http://server/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getNode", namespace = "http://server/")
public class GetNode {
/**
 * so as to get a generated strategy.
 */

    @XmlElement(name = "arg0", namespace = "")
    private String arg0;

    /**
     * @return returns String
     */
    public final String getArg0() {
        return this.arg0;
    }

    /**
     * @param arg0 the value for the arg0 property
     */
    public final void setArg0(final String arg0) {
        this.arg0 = arg0;
    }


}
