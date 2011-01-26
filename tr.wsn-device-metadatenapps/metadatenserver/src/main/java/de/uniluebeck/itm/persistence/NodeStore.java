package de.uniluebeck.itm.persistence;

import de.uniluebeck.itm.metadaten.entities.Node;




public final class NodeStore extends AbstractStore<String, Node> {

    /**
     * The single instance of the object.
     */
    private static NodeStore instance = null;

    /**
     * Private constructor -- we are the only class.
     * allowed to create new instances.
     */
    private NodeStore() {
        super();
    }

    /**
     * Provides access to the unique instance of the class.
     *
     * @return an instance of the class.
     */
    public static NodeStore getInstance() {
        // Check if we have an instance
        synchronized (NodeStore.class) {
            if (instance == null) {
                // Create a new instance
                instance = new NodeStore();
            }
        }

        return instance;
    }

}
