package org.xssfinder.runner;

import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.reporting.XssJournal;

import java.util.Map;

public class AttackPageStrategy implements PageStrategy {
    private final PageAttacker pageAttacker;

    public AttackPageStrategy(PageAttacker pageAttacker) {
        this.pageAttacker = pageAttacker;
    }

    @Override
    public void processPage(PageContext pageContext, XssJournal xssJournal) throws TWebInteractionException {
        Map<String, XssDescriptor> xssIdsToXssDescriptors =
                pageAttacker.attackIfAboutToSubmit(pageContext);
        for (Map.Entry<String, XssDescriptor> entry : xssIdsToXssDescriptors.entrySet()) {
            xssJournal.addXssDescriptor(entry.getKey(), entry.getValue());
        }
    }
}
