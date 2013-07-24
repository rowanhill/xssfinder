package org.xssfinder.xss;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.TWebInteractionException;

import java.util.Collections;
import java.util.Set;

/**
 * Looks for successful attacks on the current page
 */
public class XssDetector {
    public Set<String> getCurrentXssIds(ExecutorWrapper executor) throws TWebInteractionException {
        Set<String> currentXssIds = executor.getCurrentXssIds();
        if (currentXssIds == null) {
            return Collections.emptySet();
        } else {
            return currentXssIds;
        }
    }
}
