package org.xssfinder.reporting;

import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XssJournal {
    private final Map<String, XssDescriptor> descriptorsById = new HashMap<String, XssDescriptor>();
    private final Map<String, XssSighting> xssSightingsById = new HashMap<String, XssSighting>();
    private final Set<Class<?>> pagesClassesWithUntestedInputs = new HashSet<Class<?>>();
    private final XssSightingFactory xssSightingFactory;

    public XssJournal(XssSightingFactory xssSightingFactory) {
        this.xssSightingFactory = xssSightingFactory;
    }

    public void addXssDescriptor(String xssIdentifier, XssDescriptor xssDescriptor) {
        descriptorsById.put(xssIdentifier, xssDescriptor);
    }

    public XssDescriptor getDescriptorById(String xssIdentifier) {
        return descriptorsById.get(xssIdentifier);
    }

    public void markAsSuccessful(PageContext pageContext, Set<String> xssIdentifiers) {
        for (String xssIdentifier : xssIdentifiers) {
            XssDescriptor descriptor = descriptorsById.get(xssIdentifier);
            if (!xssSightingsById.containsKey(xssIdentifier)) {
                xssSightingsById.put(xssIdentifier, xssSightingFactory.createXssSighting(pageContext, descriptor));
            }
        }
    }

    public Set<XssSighting> getXssSightings() {
        return new HashSet<XssSighting>(xssSightingsById.values());
    }

    public void addPageClassWithUntestedInputs(Class<?> pageClass) {
        pagesClassesWithUntestedInputs.add(pageClass);
    }

    public Set<Class<?>> getPagesClassWithUntestedInputs() {
        return pagesClassesWithUntestedInputs;
    }
}
