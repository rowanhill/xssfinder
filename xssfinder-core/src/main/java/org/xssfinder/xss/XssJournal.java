package org.xssfinder.xss;

import java.util.HashMap;
import java.util.Map;

public class XssJournal {
    private final Map<String, XssDescriptor> descriptorsById = new HashMap<String, XssDescriptor>();

    public void addXssDescriptor(String xssIdentifier, XssDescriptor xssDescriptor) {
        descriptorsById.put(xssIdentifier, xssDescriptor);
    }

    public XssDescriptor getDescriptorById(String xssIdentifier) {
        return descriptorsById.get(xssIdentifier);
    }
}
