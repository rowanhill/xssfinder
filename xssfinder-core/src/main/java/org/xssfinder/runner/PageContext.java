package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;

import java.util.Map;

/**
 * Describes the current page and associated context whilst traversing through a route
 */
public class PageContext {
    private final ExecutorWrapper executor;
    private final XssJournal xssJournal;
    private final XssDescriptorFactory xssDescriptorFactory;
    private final PageTraversal pageTraversal;
    private final PageDescriptor pageDescriptor;

    public PageContext(
            ExecutorWrapper executor,
            XssJournal xssJournal,
            XssDescriptorFactory xssDescriptorFactory,
            PageTraversal pageTraversal,
            PageDescriptor pageDescriptor
    ) {
        this.executor = executor;
        this.xssJournal = xssJournal;
        this.xssDescriptorFactory = xssDescriptorFactory;
        this.pageTraversal = pageTraversal;
        this.pageDescriptor = pageDescriptor;
    }

    /**
     * @return True if it is possible to traverse to the next context
     * @see org.xssfinder.runner.PageContext#getNextContext()
     */
    public boolean hasNextContext() {
        return pageTraversal != null;
    }

    /**
     * @return A PageContext created by traversing to the next page in the route
     * @see org.xssfinder.runner.PageContext#hasNextContext()
     */
    public PageContext getNextContext() throws TWebInteractionException {
        if (!hasNextContext()) {
            throw new IllegalStateException();
        }
        Map<String, String> inputIdsToAttackIds = executor.traverseMethod(
                pageTraversal.getMethod(),
                pageTraversal.getTraversalMode().convertToThrift()
        );
        for (Map.Entry<String, String> inputIdToAttackId : inputIdsToAttackIds.entrySet()) {
            XssDescriptor xssDescriptor =
                    xssDescriptorFactory.createXssDescriptor(pageTraversal, inputIdToAttackId.getKey());
            xssJournal.addXssDescriptor(inputIdToAttackId.getValue(), xssDescriptor);
        }
        return new PageContext(
                executor,
                xssJournal,
                xssDescriptorFactory,
                pageTraversal.getNextTraversal(),
                pageTraversal.getResultingPageDescriptor()
        );
    }

    /**
     * @return The current page definition
     */
    public PageDefinition getPageDefinition() {
        return pageDescriptor.getPageDefinition();
    }

    /**
     * @return The Executor used to interact with the web site under test
     */
    public ExecutorWrapper getExecutor() {
        return executor;
    }

    /**
     * @return The next page traversal to be taken, or null if none
     */
    public PageTraversal getPageTraversal() {
        return pageTraversal;
    }

    /**
     * @return The page descriptor of the current page
     */
    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }
}
