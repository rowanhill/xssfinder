package org.xssfinder.runner;

import org.xssfinder.reporting.XssJournal;

interface PageStrategy {
    void processPage(PageContext pageContext, XssJournal xssJournal);
}
