package org.xssfinder.reporting;

import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

public class XssSightingFactory {
    public XssSighting createXssSighting(PageContext pageContext, XssDescriptor xssDescriptor) {
        return new XssSighting(pageContext.getPage(), xssDescriptor);
    }
}
