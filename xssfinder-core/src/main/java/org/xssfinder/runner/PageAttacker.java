package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

import java.util.HashMap;
import java.util.Map;

public class PageAttacker {
    private final XssGenerator xssGenerator;
    private final XssDescriptorFactory xssDescriptorFactory;

    public PageAttacker(XssGenerator xssGenerator, XssDescriptorFactory xssDescriptorFactory) {
        this.xssGenerator = xssGenerator;
        this.xssDescriptorFactory = xssDescriptorFactory;
    }

    public Map<String, XssDescriptor> attackIfAboutToSubmit(Object page, DriverWrapper driverWrapper, PageTraversal pageTraversal) {
        Map<String, XssDescriptor> xssIdsToDescriptors = new HashMap<String, XssDescriptor>();
        if (pageTraversal.isSubmit()) {
            Map<String, String> inputIdsToXssIds = driverWrapper.putXssAttackStringsInInputs(xssGenerator);
            for (Map.Entry<String, String> entry : inputIdsToXssIds.entrySet()) {
                XssDescriptor xssDescriptor = xssDescriptorFactory.createXssDescriptor(page, entry.getKey());
                xssIdsToDescriptors.put(entry.getValue(), xssDescriptor);
            }
        }
        return xssIdsToDescriptors;
    }
}
