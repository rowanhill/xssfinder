package org.xssfinder.xss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XssJournal {
    private final Map<String, XssDescriptor> descriptorsById = new HashMap<String, XssDescriptor>();
    private final Set<XssDescriptor> successfulDescriptors = new HashSet<XssDescriptor>();

    public void addXssDescriptor(String xssIdentifier, XssDescriptor xssDescriptor) {
        descriptorsById.put(xssIdentifier, xssDescriptor);
    }

    public XssDescriptor getDescriptorById(String xssIdentifier) {
        return descriptorsById.get(xssIdentifier);
    }

    public void markAsSuccessful(Set<String> xssIdentifiers) {
        for (String xssIdentifier : xssIdentifiers) {
            successfulDescriptors.add(descriptorsById.get(xssIdentifier));
        }
    }

    public Set<XssDescriptor> getSuccessfulXssDescriptors() {
        return successfulDescriptors;
    }
}
