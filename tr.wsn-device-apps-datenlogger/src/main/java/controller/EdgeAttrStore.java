package controller;

import model.EdgeAttribute;


public final class EdgeAttrStore extends AbstractStore<Integer, EdgeAttribute> {

    /**
     * The single instance of the object.
     */
    private static EdgeAttrStore instance = null;

    /**
     * Private constructor -- we are the only class allowed.
     * to create new instances.
     */
    private EdgeAttrStore() {
        super();
    }

    /**
     * Provides access to the unique instance of the class.
     *
     * @return an instance of the class.
     */
    public static EdgeAttrStore getInstance() {
        // Check if we have an instance
        synchronized (EdgeAttrStore.class) {
            if (instance == null) {
                // Create a new instance
                instance = new EdgeAttrStore();
            }
        }

        return instance;
    }


}


