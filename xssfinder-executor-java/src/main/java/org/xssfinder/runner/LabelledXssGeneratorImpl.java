package org.xssfinder.runner;

import org.xssfinder.LabelledXssGenerator;

public class LabelledXssGeneratorImpl implements LabelledXssGenerator {
    /*private final XssGenerator xssGenerator;
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
    }*/

    @Override
    public String getXssAttackTextForLabel(String label) {
        /*XssAttack attack = xssGenerator.createXssAttack();
        XssDescriptor descriptor = xssDescriptorFactory.createXssDescriptor(pageTraversal, label);
        xssJournal.addXssDescriptor(attack.getIdentifier(), descriptor);
        return attack.getAttackString();
        */
        return null;
    }
}
