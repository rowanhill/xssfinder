package org.xssfinder.xss;

public class XssAttack {
    private static final String ATTACK_TEMPLATE =
            "<script type=\"text/javascript\">" +
                "if (typeof(window.xssfinder) === \"undefined\"){" +
                    "window.xssfinder = [];" +
                "}"+
                "window.xssfinder.push('%s');" +
            "</script>";

    private final String identifier;

    public XssAttack(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAttackString() {
        return String.format(ATTACK_TEMPLATE, identifier);
    }
}
