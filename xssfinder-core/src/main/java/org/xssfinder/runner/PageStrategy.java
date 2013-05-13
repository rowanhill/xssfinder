package org.xssfinder.runner;

import org.xssfinder.xss.XssJournal;

public interface PageStrategy {
    void processPage(PageContext pageContext, XssJournal xssJournal);
}
