package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
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
    public void processPage(Object page, PageTraversal nextTraversal, DriverWrapper driverWrapper) {
        Map<String, XssDescriptor> xssIdsToXssDescriptors =
                pageAttacker.attackIfAboutToSubmit(page, driverWrapper, nextTraversal);
        for (Map.Entry<String, XssDescriptor> entry : xssIdsToXssDescriptors.entrySet()) {
            xssJournal.addXssDescriptor(entry.getKey(), entry.getValue());
        }
    }
}
