package org.xssfinder.reporting;

import org.xssfinder.xss.XssDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlReportWriter {
    private final String outFilePath;

    public HtmlReportWriter(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    public void write(XssJournal journal) throws IOException {
        File outFile = createOutfile();
        BufferedWriter output = new BufferedWriter(new FileWriter(outFile));
        try {
            output.write("<html>");
            output.write("<head>");
            output.write("<title>XssFinder Report</title>");
            output.write("</head>");
            output.write("<body>");
            output.write("<p>The following vulnerabilities were detected:</p>");
            output.write("<table id='vulnerabilities'>");
            output.write("<tr>");
            output.write("<th>Page Object</th><th>Input XPath</th>");
            output.write("</tr>");
            for (XssDescriptor descriptor : journal.getSuccessfulXssDescriptors()) {
                output.write(
                        "<tr>" +
                            "<td>" +
                                descriptor.getPageClass().getCanonicalName() +
                            "</td>" +
                            "<td>" +
                                descriptor.getInputIdentifier() +
                            "</td>" +
                        "</tr>"
                );
            }
            output.write("</table>");
            output.write("</body>");
            output.write("</html>");
        } finally {
            output.close();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createOutfile() throws IOException {
        File outFile = new File(outFilePath);
        outFile.createNewFile();
        return outFile;
    }
}
