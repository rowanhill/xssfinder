package org.xssfinder.xss;

public class XssGenerator {
    private int nextXssId = 1;

    public String createXssString() {
        String xss =
                "<script type=\"text/javascript\">" +
                    "if (typeof(window.xssfinder) === \"undefined\"){" +
                        "window.xssfinder = [];" +
                    "}"+
                    String.format("window.xssfinder.push(%d);", nextXssId) +
                "</script>";
        nextXssId++;
        return xss;
    }
}
