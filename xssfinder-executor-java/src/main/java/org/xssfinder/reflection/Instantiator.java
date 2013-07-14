package org.xssfinder.reflection;

import java.lang.reflect.Constructor;

public class Instantiator {
    /**
     * Create an instance of the given class, using the null constructor.
     *
     * @param clazz The class to create an instance of
     * @param <T> The type of the returned object
     * @return An object of type T
     * @throws InstantiationException If a null constructor cannot be found, or an exception is thrown in construction
     */
    public <T> T instantiate(Class<T> clazz) throws InstantiationException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new InstantiationException(e);
        }
    }
}
