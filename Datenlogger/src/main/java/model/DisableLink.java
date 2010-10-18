package model;

import org.simpleframework.xml.Attribute;

/**
 * This class describes the  DisableLink element of the scenario section in a wiseml file.
 */
public class DisableLink {

    /**
     * The edge's source.
     */

    @Attribute
    private String source;

    /**
     * The edge's target.
     */

    @Attribute
    private String target;

    /**
     * Requires function for deserializing objects.
     */
    public DisableLink() {
        super();
    }


    /**
     * Constructor Method.
     *
     * @param source
     * @param target
     */
    public DisableLink(String source, String target) {

        setSource(source);
        setTarget(target);
    }

    /**
     * Returns the source node of this entity.
     *
     * @return the source.
     */
    public final String getSource() {
        return this.source;
    }


    /**
     * Sets the source node  for this entity.
     *
     * @param newSource the new name.
     */
    public final void setSource(final String newSource) {
        this.source = newSource;
    }

    /**
     * Returns the target node of this entity.
     *
     * @return the target.
     */
    public final String getTarget() {
        return this.target;
    }


    /**
     * Sets the target node for this entity.
     *
     * @param newTarget the new name.
     */
    public final void setTarget(final String newTarget) {
        this.target = newTarget;
    }


}
