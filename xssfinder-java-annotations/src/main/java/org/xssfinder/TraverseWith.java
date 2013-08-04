package org.xssfinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a method on a page object should be traversed using a CustomTraverser, rather than simply invoking the
 * method on the page object.
 *
 * If a traversal method has parameters, a CustomTraverser is required. An error will be encountered if one is not
 * provided.
 *
 * @see CustomTraverser#traverse(Object)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TraverseWith {

    /**
     * Optional argument specifying the Class of a CustomTraverser to be used when invoking the annotated method.
     *
     * @return The Class of a CustomTraverser to use.
     */
    public Class<? extends CustomTraverser> value();
}
