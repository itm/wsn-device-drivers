package model;

import org.simpleframework.xml.Element;

/**
 * This class defines the origin element,  in a wiseml  file.
 */
public class Origin {

    /**
     * Setting up the properties section.
     */
    @Element
    private Position position;

    @Element
    private double phi;


    @Element
    private double theta;

    public Origin() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param position
     * @param phi
     * @param theta
     */
    public Origin(Position position, double phi, double theta) {
        this.position = position;
        this.phi = phi;
        this.theta = theta;
    }

    /**
     * Return the position element.
     *
     * @return
     */
    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Returns the phi variable of the entity.
     *
     * @return double phi
     */
    public double getPhi() {
        return this.phi;
    }

    /**
     * Sets the phi variable of the origin entity.
     *
     * @param phi
     */
    public void setPhi(final double phi) {
        this.phi = phi;
    }

    /**
     * Returns the theta variable of the entity.
     *
     * @return double theta.
     */
    public double getTheta() {
        return this.theta;
    }

    /**
     * Sets the theta variable of the origin entity.
     *
     * @param theta
     */
    public void setTheta(final double theta) {
        this.theta = theta;
    }

}
