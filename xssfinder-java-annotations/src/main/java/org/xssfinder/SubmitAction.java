package org.xssfinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a method on a page object as being a submit method. Such methods will typically post a form.
 *
 * XSS Finder will execute all @SubmitAction annotated methods, attempting to fill all inputs with XSS attacks just
 * prior to invoking the submit methods.
 *
 * XSS Finder will attempt to report on missing @SubmitAction methods (by counting the number of forms on a page and
 * generating a warning if it exceeds the number of annotated methods on the corresponding page object). Such warnings
 * are non-fatal.
 *
 * If a submit method has parameters, a CustomSubmitter is required. An error will be encountered if one is not provided.
 *
 * @see CustomSubmitter#submit(Object, LabelledXssGenerator)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubmitAction {

    /**
     * Optional argument specifying the Class of a CustomSubmitter to be used when invoking the annotated submit method.
     *
     * @return The Class of a CustomSubmitter to use.
     */
    public Class<? extends CustomSubmitter> value() default CustomSubmitter.class; // The interface is used as a 'null' value
}
