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
            output.write("<th>Input Page Object</th><th>Submit Method</th><th>Input XPath</th><th>Sighting Page Object</th>");
            output.write("</tr>");
            for (XssSighting xssSighting : journal.getXssSightings()) {
                output.write(
                        "<tr>" +
                            "<td>" +
                                xssSighting.getVulnerableClassName() +
                            "</td>" +
                            "<td>" +
                                xssSighting.getSubmitMethodName() + "()" +
                            "</td>" +
                            "<td>" +
                                xssSighting.getInputIdentifier() +
                            "</td>" +
                            "<td>" +
                                xssSighting.getSightingClassName() +
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
