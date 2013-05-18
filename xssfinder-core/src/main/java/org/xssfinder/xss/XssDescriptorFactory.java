package org.xssfinder.xss;

import org.xssfinder.routing.PageTraversal;

public class XssDescriptorFactory {
    public XssDescriptor createXssDescriptor(PageTraversal pageTraversal, String inputIdentifier) {
        return new XssDescriptor(pageTraversal.getMethod(), inputIdentifier);
    }
}
