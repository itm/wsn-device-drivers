package server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * so as to get a generated strategy.
 */
@XmlRootElement(name = "getDescriptionResponse", namespace = "http://server/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDescriptionResponse", namespace = "http://server/")

/**
 * so as to get a generated strategy.
 */
public class GetDescriptionResponse {
/**
 * so as to get a generated strategy.
 */
    @XmlElement(name = "return", namespace = "")
    private String _return;

    /**
     * @return returns String
     */
    public final String get_return() {
        return this._return;
    }

    /**
     * @param _return the value for the _return property
     */
    public final void set_return(final String _return) {
        this._return = _return;
    }

}
