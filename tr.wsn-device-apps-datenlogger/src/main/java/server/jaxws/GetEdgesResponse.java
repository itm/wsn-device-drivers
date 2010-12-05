package server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "getEdgesResponse", namespace = "http://server/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEdgesResponse", namespace = "http://server/")

public class GetEdgesResponse {

    @XmlElement(name = "return", namespace = "")
    private String _return;

    /**
     * @return returns String
     */
    public String get_return() {
        return this._return;
    }

    /**
     * @param _return the value for the _return property
     */
    public void set_return(final String _return) {
        this._return = _return;
    }


}
