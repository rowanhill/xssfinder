package org.xssfinder.xss;

public class XssAttackFactory {
    public XssAttack createXssAttack(String identifier) {
        return new XssAttack(identifier);
    }
}
