package org.xssfinder.runner;

public class LifecycleEventException extends RuntimeException {
    public LifecycleEventException(String message) {
        super(message);
    }

    public LifecycleEventException(Exception exception) {
        super(exception);
    }
}
