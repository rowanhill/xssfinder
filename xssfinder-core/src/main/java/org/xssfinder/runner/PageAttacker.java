package org.xssfinder.runner;

import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

import java.util.HashMap;
import java.util.Map;

class PageAttacker {
    private final XssGenerator xssGenerator;
    private final XssDescriptorFactory xssDescriptorFactory;

    public PageAttacker(XssGenerator xssGenerator, XssDescriptorFactory xssDescriptorFactory) {
        this.xssGenerator = xssGenerator;
        this.xssDescriptorFactory = xssDescriptorFactory;
    }

    /**
     * Attack the inputs in the current page if the next traversal is a submit action
     *
     * @param pageContext The current page context
     * @return A map of XSS attack identifiers to XssDescriptors
     */
    public Map<String, XssDescriptor> attackIfAboutToSubmit(PageContext pageContext) {
        Map<String, XssDescriptor> xssIdsToDescriptors = new HashMap<String, XssDescriptor>();
        if (pageContext.hasNextContext() && pageContext.getPageTraversal().isSubmit()) {
            Map<String, String> inputIdsToXssIds = pageContext.getDriverWrapper().putXssAttackStringsInInputs(xssGenerator);
            for (Map.Entry<String, String> entry : inputIdsToXssIds.entrySet()) {
                XssDescriptor xssDescriptor = xssDescriptorFactory.createXssDescriptor(pageContext.getPageTraversal(), entry.getKey());
                xssIdsToDescriptors.put(entry.getValue(), xssDescriptor);
            }
        }
        return xssIdsToDescriptors;
    }
}
