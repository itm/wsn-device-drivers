package controller;

import model.Link;


public final class EdgeStore extends AbstractStore<Integer, Link> {

    /**
     * The single instance of the object.
     */
    private static EdgeStore instance = null;

    /**
     * Private constructor -- we are the only class.
     * allowed to create new instances.
     */
    private EdgeStore() {
        super();
    }

    /**
     * Provides access to the unique instance of the class.
     *
     * @return an instance of the class.
     */
    public static EdgeStore getInstance() {
        // Check if we have an instance
        synchronized (EdgeStore.class) {
            if (instance == null) {
                // Create a new instance
                instance = new EdgeStore();
            }
        }

        return instance;
    }
}
