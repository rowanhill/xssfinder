package org.xssfinder;

/**
 * Specifies an interface that custom submitter classes must conform to. Custom submitters are used when attacking a
 * "complex" submit action (i.e. one with parameters, as XSS Finder cannot handle them by default).
 *
 * Custom submitters are specified by @SubmitAction annotations.
 *
 * @see org.xssfinder.SubmitAction#value()
 * @see CustomTraverser
 */
public interface CustomSubmitter {

    /**
     * When invoked, this method should set up the give page with XSS attacks in its inputs (using the
     * LabelledXssGenerator), perform any steps necessary to successfully submit the page, and then do so. This is
     * typically achieved by calling the relevant submit method on {@code page} with arguments that include XSS attacks
     * generated by {@code xssGenerator}.
     *
     * Note that {@code xssGenerator} should always be used (instead of hand-crafted XSS attacks), as otherwise XSS
     * Finder is unaware of the attack, and will not search for it or report on it.
     *
     * @param page The current page being submitted
     * @param xssGenerator A generator of XSS attack strings
     * @return The page resulting from submission
     */
    Object submit(Object page, LabelledXssGenerator xssGenerator);
}
