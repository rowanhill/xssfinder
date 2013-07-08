package org.xssfinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a method as a handler for the AfterRoute lifecycle event on a lifecycle event handler class.
 *
 * The AfterRoute event is fired after running a route has either executed to completion or has thrown an error.
 *
 * @see org.xssfinder.CrawlStartPoint#lifecycleHandler()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterRoute {
}
