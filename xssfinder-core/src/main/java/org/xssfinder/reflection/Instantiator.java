package org.xssfinder.reflection;

import java.lang.reflect.Constructor;

public class Instantiator {
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
