package server.jaxws;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * so as to get a generated strategy.
 */
@XmlRootElement(name = "getMultiRecords", namespace = "http://server/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMultiRecords", namespace = "http://server/")
public class GetMultiRecords {
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
/**
 * so as to get a generated strategy.
 */
    @XmlElement(name = "arg1", namespace = "")
    private String arg1;

    /**
     * @return returns String
     */
    public final String getArg1() {
        return this.arg1;
    }

    /**
     * @param arg1 the value for the arg0 property
     */
    public final void setArg1(final String arg1) {
        this.arg1 = arg1;
    }
/**
 * so as to get a generated strategy.
 */
    @XmlElement(name = "arg2", namespace = "")
    private String arg2;

    /**
     * @return returns String
     */
    public final String getArg2() {
        return this.arg2;
    }

    /**
     * @param arg2 the value for the arg0 property
     */
    public final void setArg2(final String arg2) {
        this.arg2 = arg2;
    }
 /**
 * so as to get a generated strategy.
 */

    @XmlElement(name = "arg3", namespace = "")
    private String arg3;

    /**
     * @return returns String
     */
    public final String getArg3() {
        return this.arg3;
    }

    /**
     * @param arg3 the value for the arg0 property
     */
    public final void setArg3(final String arg3) {
        this.arg3 = arg3;
    }

}
