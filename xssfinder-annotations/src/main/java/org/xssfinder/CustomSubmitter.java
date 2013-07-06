package org.xssfinder;

public interface CustomSubmitter {
    Object submit(Object page, LabelledXssGenerator xssGenerator);
}
