package eu.smartsantander.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @author TLMAT UC
 */
public class LRUcache<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	protected int maxElements;

	public LRUcache(int maxSize) {
		super(maxSize, 0.75f, true);
		this.maxElements = maxSize;
	}

	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return (size() > this.maxElements);
	}
}