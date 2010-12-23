package de.uniluebeck.itm.persistence;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniluebeck.itm.metadaten.entities.Key;

/**
 * @param <K>
 * @param <V>
 */
public abstract class AbstractStore<K, V extends Key> {

    private final Map<K, V> store;

    protected AbstractStore() {
        this.store = new HashMap<K, V>();
    }

    /**
     * Adds a new value in the store.
     *
     * @param value
     */
    public final void add(final V value) {
        store.put((K) value.getKey(), value);
    }

    /**
     * Looksup the  store and retrieves values based on the key.
     *
     * @param key
     * @return
     */
    public final V get(final K key) {
        return store.get(key);
    }


    /**
     * Returns the number of entities stored in the hashmap.
     *
     * @return the size of the hashmap.
     */
    public final int size() {
        return store.size();
    }

    /**
     * @return a list of all the values in the store.
     */
    public final List<V> list() {
        return new ArrayList<V>(store.values());
    }


}
