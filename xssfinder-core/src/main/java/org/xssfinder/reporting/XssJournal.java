package org.xssfinder.reporting;

import org.xssfinder.remote.PageDefinition;
import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import java.util.*;

/**
 * A journal of the XSS attacks performed, which have been successful, and which pages may need more @SubmitActions
 */
public class XssJournal {
    private final Map<String, XssDescriptor> descriptorsById = new HashMap<String, XssDescriptor>();
    private final Map<String, XssSighting> xssSightingsById = new HashMap<String, XssSighting>();
    private final Set<PageDefinition> pagesDefinitionsWithUntestedInputs = new HashSet<PageDefinition>();
    private final List<RouteRunErrorContext> errorContexts = new ArrayList<RouteRunErrorContext>();
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

    public void addPageClassWithUntestedInputs(PageDefinition pageClass) {
        pagesDefinitionsWithUntestedInputs.add(pageClass);
    }

    public Set<PageDefinition> getPagesClassWithUntestedInputs() {
        return pagesDefinitionsWithUntestedInputs;
    }

    public void addErrorContext(RouteRunErrorContext errorContext) {
        errorContexts.add(errorContext);
    }

    public List<RouteRunErrorContext> getErrorContexts() {
        return errorContexts;
    }
}
