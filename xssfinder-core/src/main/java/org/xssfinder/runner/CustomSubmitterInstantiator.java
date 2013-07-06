package org.xssfinder.runner;

import org.xssfinder.CustomSubmitter;
import org.xssfinder.SubmitAction;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;

import java.lang.reflect.Method;

public class CustomSubmitterInstantiator {
    private final Instantiator instantiator;

    public CustomSubmitterInstantiator(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public CustomSubmitter instantiate(Method method) {
        SubmitAction submitAction = method.getAnnotation(SubmitAction.class);
        if (submitAction == null) {
            return null;
        }
        Class<? extends CustomSubmitter> submitterClass = submitAction.value();
        if (submitterClass == CustomSubmitter.class) {
            // CustomSubmitter is the default value, and indicates that no explicit custom submitter is given
            return null;
        }
        try {
            return instantiator.instantiate(submitterClass);
        } catch (InstantiationException e) {
            throw new CustomSubmitterInstantiationException(e);
        }
    }
}
