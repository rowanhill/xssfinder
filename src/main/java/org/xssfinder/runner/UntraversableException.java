package org.xssfinder.runner;

public class UntraversableException extends RuntimeException {
    public UntraversableException(String message) {
        super(message);
    }
    public UntraversableException(Exception e) {
        super(e);
    }
}
