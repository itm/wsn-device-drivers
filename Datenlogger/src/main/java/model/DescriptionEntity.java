package model;

import javax.persistence.*;

/**
 * A Description Table Entity is described with Hibernate.
 */

@Entity
@Table(catalog = "graph", name = "description")
public class DescriptionEntity {

    private String sensorType;

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    @Id
    @Column(name = "sensorType", nullable = false, length = 100)
    public String getType() {
        return sensorType;
    }

    public void setType(String sensorType) {
        this.sensorType = sensorType;
    }

    private String accuracy;

    @Basic
    @Column(name = "accuracy", nullable = true, length = 100)
    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }


    private String info;

    @Basic
    @Column(name = "info", nullable = false, length = 100)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String range;

    @Basic
    @Column(name = "range", nullable = true, length = 100)
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    private String resolution;

    @Basic
    @Column(name = "resolution", nullable = true)
    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    private String uom;

    @Basic
    @Column(name = "uom", nullable = true, length = 100)
    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DescriptionEntity that = (DescriptionEntity) o;


        if (sensorType != null ? !sensorType.equals(that.sensorType) : that.sensorType != null) return false;
        if (accuracy != null ? !accuracy.equals(that.accuracy) : that.accuracy != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;
        if (range != null ? !range.equals(that.range) : that.range != null) return false;
        if (resolution != null ? !resolution.equals(that.resolution) : that.resolution != null) return false;
        if (sensorType != null ? !sensorType.equals(that.sensorType) : that.sensorType != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = sensorType != null ? sensorType.hashCode() : 0;
        result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (range != null ? range.hashCode() : 0);
        result = 31 * result + (uom != null ? uom.hashCode() : 0);
        return result;

    }

}