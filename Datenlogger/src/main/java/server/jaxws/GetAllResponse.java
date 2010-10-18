package server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "getAllResponse", namespace = "http://server/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllResponse", namespace = "http://server/")
public class GetAllResponse {


    @XmlElement(name = "return", namespace = "")
    private String returnValue;

    /**
     * @return returns String
     */
    public String getReturnValue() {
        return this.returnValue;
    }

    /**
     * @param returnValue the value for the _return property
     */
    public void setReturnValue(final String returnValue) {
        this.returnValue = returnValue;
    }

}
