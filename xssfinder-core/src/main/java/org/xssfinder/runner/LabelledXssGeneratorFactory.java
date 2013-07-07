package org.xssfinder.runner;

import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

public class LabelledXssGeneratorFactory {
    public LabelledXssGenerator createLabelledXssGenerator(PageTraversal pageTraversal, XssJournal xssJournal) {
        return new LabelledXssGeneratorImpl(
                new XssGenerator(new XssAttackFactory()),
                xssJournal,
                new XssDescriptorFactory(),
                pageTraversal
        );
    }
}
