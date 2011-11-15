package eu.smartsantander.util.guice;


import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.internal.CircularDependencyProxy;
import com.google.inject.internal.InternalInjectorCreator;

/**
 * @author TLMAT UC
 */
public class CustomScopes {

    /**
     * A sentinel value representing null.
     */
    private static final Object NULL = new Object();

    private static Object instance = null;

    public static final Scope JVM_SINGLETON = new Scope() {

        @Override
        public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
            return new Provider<T>() {
                // DCL on a volatile is safe as of Java 5, which we obviously require.
                @SuppressWarnings("DoubleCheckedLocking")
                public T get() {
                    if (instance == null) {
                        /*
                        * Use a pretty coarse lock. We don't want to run into deadlocks
                        * when two threads try to load circularly-dependent objects.
                        * Maybe one of these days we will identify independent graphs of
                        * objects and offer to load them in parallel.
                        *
                        * This block is re-entrant for circular dependencies.
                        */
                        synchronized (InternalInjectorCreator.class) {
                            if (instance == null) {
                                T provided = creator.get();

                                // don't remember proxies; these exist only to serve circular dependencies
                                if (provided instanceof CircularDependencyProxy) {
                                    return provided;
                                }

                                Object providedOrSentinel = (provided == null) ? NULL : provided;
                                if (instance != null && instance != providedOrSentinel) {
                                    throw new ProvisionException(
                                            "Provider was reentrant while creating a singleton");
                                }

                                instance = providedOrSentinel;
                            }
                        }
                    }

                    Object localInstance = instance;
                    // This is safe because instance has type T or is equal to NULL
                    @SuppressWarnings("unchecked")
                    T returnedInstance = (localInstance != NULL) ? (T) localInstance : null;
                    return returnedInstance;
                }

                public String toString() {
                    return String.format("%s[%s]", creator, JVM_SINGLETON);
                }
            };

        }

    };
}

