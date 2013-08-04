package org.xssfinder.xss;

/**
 * Generates uniquely identifiable XSS attacks
 */
public class XssGenerator {
    private final XssAttackFactory xssAttackFactory;
    private int nextXssId = 1;

    public XssGenerator(XssAttackFactory xssAttackFactory) {
        this.xssAttackFactory = xssAttackFactory;
    }

    public XssAttack createXssAttack() {
        XssAttack attack = xssAttackFactory.createXssAttack(Integer.toString(nextXssId));
        nextXssId++;
        return attack;
    }
}
