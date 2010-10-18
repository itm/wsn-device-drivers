package model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * An Edge Entity is described.
 */
public class Edge implements Key {
    /**
     * The edge id.
     */
    @Attribute
    private String id;
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
     * List with the edgeAttribute elements of the entity.
     */
    @ElementList(inline = true)
    private List<EdgeAttribute> edgeAttributeList;

    /**
     * @return edgeAttributeList.
     */
    public final List getEdgeAttributeList() {
        return edgeAttributeList;
    }

    /**
     * Constructor.
     *
     * @param id
     * @param source
     * @param target
     * @param attrList
     */
    public Edge(final String id, final String source,
                final String target, final List attrList) {
        this.id = id;
        setID(id);
        setSource(source);
        setTarget(target);
        edgeAttributeList = attrList;
    }

    public Edge() {
    }

    /**
     * Returns the identity of this entity.
     *
     * @return the identity.
     */
    public final String getID() {
        return this.id;
    }


    /**
     * Sets the id for this entity.
     *
     * @param newId the new name.
     */
    public final void setID(final String newId) {
        this.id = newId;
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

    /**
     * impements the getKey function from Interface Key.
     *
     * @return the Object(integer) Key of the entity Node.
     */
    public final Object getKey() {
        return getID();
    }

}
