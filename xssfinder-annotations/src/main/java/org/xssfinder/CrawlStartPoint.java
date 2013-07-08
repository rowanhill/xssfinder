package org.xssfinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a page is a starting point for crawling the site under test. Only one such page may exist per connected
 * graph of pages. Classes annotated with @CrawlStartPoint must also be annotated with @Page.
 *
 * @see Page
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CrawlStartPoint {
    /**
     * Specifies the URL to visit when beginning routes that start at this page.
     *
     * @return The string URL of the annotated page.
     */
    public String url();

    /**
     * Optional parameter specifying a lifecycle event handler class. Such a class should have methods annotated for
     * handling lifecycle events.
     *
     * @return The Class of the lifecycle event handler class.
     *
     * @see AfterRoute
     */
    public Class<?> lifecycleHandler() default Object.class; // Object.class must be literal
}
