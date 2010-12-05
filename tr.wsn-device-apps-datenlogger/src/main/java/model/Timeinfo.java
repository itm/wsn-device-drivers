package model;

import org.simpleframework.xml.Element;

/**
 * This class describes the time info section of a wiseml file.
 */


public class Timeinfo {

    /**
     * Start time of an experiment.
     */
    @Element
    private String start;

    /**
     * End time of an experiment.
     */
    @Element
    private String end;

    /**
     * Time unit.
     */
    @Element
    private String unit;


    /**
     * Requires function for deserializing objects.
     */
    public Timeinfo() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param start
     * @param end
     * @param unit
     */
    public Timeinfo(String start, String end, String unit) {
        setStart(start);
        setEnd(end);
        setUnit(unit);
    }

    /**
     * Get start entity.
     *
     * @return
     */
    public String getStart() {
        return this.start;
    }

    /**
     * Set start entity.
     *
     * @param start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Get end entity.
     *
     * @return
     */
    public String getEnd() {
        return this.end;
    }

    /**
     * Set end entity.
     *
     * @param end
     */
    public void setEnd(String end) {
        this.end = end;
    }


    /**
     * Get unit entity.
     *
     * @return
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Set unit entity.
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

}
