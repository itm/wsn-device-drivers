package de.uniluebeck.itm.wsn.drivers.core.util;

/**
 * Utility methods for class operations.
 * 
 * @author Malte Legenhausen
 */
public final class ClassUtil {

	private ClassUtil() {
		
	}
	
	@SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>) aClass;
    }
}
