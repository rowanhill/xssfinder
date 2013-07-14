package org.xssfinder.runner;

import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

public class LabelledXssGeneratorImpl implements LabelledXssGenerator {
    private final XssGenerator xssGenerator;
    private final XssJournal xssJournal;
    private final XssDescriptorFactory xssDescriptorFactory;
    private final PageTraversal pageTraversal;

    public LabelledXssGeneratorImpl(
            XssGenerator xssGenerator,
            XssJournal xssJournal,
            XssDescriptorFactory xssDescriptorFactory,
            PageTraversal pageTraversal) {
        this.xssGenerator = xssGenerator;
        this.xssJournal = xssJournal;
        this.xssDescriptorFactory = xssDescriptorFactory;
        this.pageTraversal = pageTraversal;
    }

    @Override
    public String getXssAttackTextForLabel(String label) {
        XssAttack attack = xssGenerator.createXssAttack();
        XssDescriptor descriptor = xssDescriptorFactory.createXssDescriptor(pageTraversal, label);
        xssJournal.addXssDescriptor(attack.getIdentifier(), descriptor);
        return attack.getAttackString();
    }
}
