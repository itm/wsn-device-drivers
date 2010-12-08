//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.06 at 07:29:07 PM MEZ 
//


package de.uniluebeck.itm.tcp.Server.JaxbDevices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jaxbDevice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jaxbDevice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="KnotenTyp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Port" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TestBed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Sensors" type="{rpc_pro/rpcPrototype/schema/devices}jaxbSensors" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jaxbDevice", propOrder = {
    "knotenTyp",
    "port",
    "testBed",
    "sensors"
})
public class JaxbDevice {

    @XmlElement(name = "KnotenTyp", required = true)
    protected String knotenTyp;
    @XmlElement(name = "Port", required = true)
    protected String port;
    @XmlElement(name = "TestBed")
    protected String testBed;
    @XmlElement(name = "Sensors")
    protected JaxbSensors sensors;

    /**
     * Gets the value of the knotenTyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKnotenTyp() {
        return knotenTyp;
    }

    /**
     * Sets the value of the knotenTyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKnotenTyp(String value) {
        this.knotenTyp = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPort(String value) {
        this.port = value;
    }

    /**
     * Gets the value of the testBed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestBed() {
        return testBed;
    }

    /**
     * Sets the value of the testBed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestBed(String value) {
        this.testBed = value;
    }

    /**
     * Gets the value of the sensors property.
     * 
     * @return
     *     possible object is
     *     {@link JaxbSensors }
     *     
     */
    public JaxbSensors getSensors() {
        return sensors;
    }

    /**
     * Sets the value of the sensors property.
     * 
     * @param value
     *     allowed object is
     *     {@link JaxbSensors }
     *     
     */
    public void setSensors(JaxbSensors value) {
        this.sensors = value;
    }

}
