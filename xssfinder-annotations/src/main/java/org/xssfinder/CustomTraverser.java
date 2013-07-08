package org.xssfinder;

/**
 * Specifies an interface that custom traverser classes must conform to. Custom traversers are used when traversing a
 * "complex" action (i.e. one with parameters, as XSS Finder cannot handle them by default).
 *
 * Custom traversers are specified by @TraverseWith annotations.
 *
 * @see org.xssfinder.TraverseWith#value()
 * @see CustomSubmitter
 */
public interface CustomTraverser {
    /**
     * When invoked, implementations of {@code traverse} typically calls the relevant method on {@code page} with
     * arguments crafted to ensure the method will complete successfully and return the expected page.
     *
     * @param page The current page to be traversed away from
     * @return The page resulting from navigation
     */
    Object traverse(Object page);
}
