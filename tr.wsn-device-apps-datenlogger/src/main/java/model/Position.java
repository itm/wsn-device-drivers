package model;

import org.simpleframework.xml.Element;

/**
 * This class describes the position element , required in a wiseml file.
 */

public class Position {

    /**
     * Setting up the properties section.
     */
    @Element
    private double x;


    @Element
    private double y;


    @Element
    private double z;

    public Position() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param x
     * @param y
     * @param z
     */
    public Position(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    /**
     * Returns the x variable of the entity.
     *
     * @return double x.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Sets the x variable of the origin entity.
     *
     * @param x
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * Returns the y variable of the entity.
     *
     * @return double y.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the y variable of the origin entity.
     *
     * @param y
     */
    public void setY(final double y) {
        this.y = y;
    }

    /**
     * Returns the z variable of the entity.
     *
     * @return double z
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Set the z variable of the origin entity.
     *
     * @param z
     */
    public void setZ(final double z) {
        this.z = z;
    }

}
