package org.xssfinder.runner;

import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssGenerator;

public class LabelledXssGeneratorFactory {

    public LabelledXssGeneratorImpl createLabelledXssGenerator() {
        return new LabelledXssGeneratorImpl(
                new XssGenerator(new XssAttackFactory())
        );
    }
}
