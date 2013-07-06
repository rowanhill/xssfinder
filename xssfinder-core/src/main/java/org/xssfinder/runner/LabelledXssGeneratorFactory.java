package org.xssfinder.runner;

import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSightingFactory;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

public class LabelledXssGeneratorFactory {
    public LabelledXssGenerator createLabelledXssGenerator(PageTraversal pageTraversal) {
        return new LabelledXssGeneratorImpl(
                new XssGenerator(new XssAttackFactory()),
                new XssJournal(new XssSightingFactory()),
                new XssDescriptorFactory(),
                pageTraversal
        );
    }
}
