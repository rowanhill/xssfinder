package org.xssfinder.runner;

import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssGenerator;

import java.util.HashMap;
import java.util.Map;

public class LabelledXssGeneratorImpl implements LabelledXssGenerator {
    private final XssGenerator xssGenerator;
    private final Map<String, String> labelsToAttackIds;

    public LabelledXssGeneratorImpl(XssGenerator xssGenerator) {
        this.xssGenerator = xssGenerator;
        this.labelsToAttackIds = new HashMap<String, String>();
    }

    @Override
    public String getXssAttackTextForLabel(String label) {
        XssAttack attack = xssGenerator.createXssAttack();
        labelsToAttackIds.put(label, attack.getIdentifier());
        return attack.getAttackString();
    }

    public Map<String, String> getLabelsToAttackIds() {
        return labelsToAttackIds;
    }
}
