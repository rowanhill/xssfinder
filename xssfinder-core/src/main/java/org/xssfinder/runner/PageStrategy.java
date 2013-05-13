package org.xssfinder.runner;

import org.xssfinder.reporting.XssJournal;

public interface PageStrategy {
    void processPage(PageContext pageContext, XssJournal xssJournal);
}
