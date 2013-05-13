package org.xssfinder.runner;

import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssJournal;

import java.util.Map;

public class AttackPageStrategy implements PageStrategy {
    private final PageAttacker pageAttacker;
    private final XssJournal xssJournal;

    public AttackPageStrategy(PageAttacker pageAttacker, XssJournal xssJournal) {
        this.pageAttacker = pageAttacker;
        this.xssJournal = xssJournal;
    }

    @Override
    public void processPage(PageContext pageContext) {
        Map<String, XssDescriptor> xssIdsToXssDescriptors =
                pageAttacker.attackIfAboutToSubmit(pageContext);
        for (Map.Entry<String, XssDescriptor> entry : xssIdsToXssDescriptors.entrySet()) {
            xssJournal.addXssDescriptor(entry.getKey(), entry.getValue());
        }
    }
}
