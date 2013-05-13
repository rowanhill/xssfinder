package org.xssfinder.runner;

import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssJournal;

import java.util.Map;

public class AttackPageStrategy implements PageStrategy {
    private final PageAttacker pageAttacker;

    public AttackPageStrategy(PageAttacker pageAttacker) {
        this.pageAttacker = pageAttacker;
    }

    @Override
    public void processPage(PageContext pageContext, XssJournal xssJournal) {
        Map<String, XssDescriptor> xssIdsToXssDescriptors =
                pageAttacker.attackIfAboutToSubmit(pageContext);
        for (Map.Entry<String, XssDescriptor> entry : xssIdsToXssDescriptors.entrySet()) {
            xssJournal.addXssDescriptor(entry.getKey(), entry.getValue());
        }
    }
}
