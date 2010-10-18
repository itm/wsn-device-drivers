package controller;

import model.NodeCapability;


public final class NodeCapabilityStore extends
        AbstractStore<Integer, NodeCapability> {

    /**
     * The single instance of the object.
     */
    private static NodeCapabilityStore instance = null;

    /**
     * Private constructor -- we are the only class.
     * allowed to create new instances.
     */
    private NodeCapabilityStore() {
        super();
    }

    /**
     * Provides access to the unique instance of the class.
     *
     * @return an instance of the class.
     */
    public static NodeCapabilityStore getInstance() {
        // Check if we have an instance
        synchronized (NodeCapabilityStore.class) {
            if (instance == null) {
                // Create a new instance
                instance = new NodeCapabilityStore();
            }
        }

        return instance;
    }


}
