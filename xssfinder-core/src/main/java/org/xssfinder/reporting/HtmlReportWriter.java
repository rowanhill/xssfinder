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
            for (XssDescriptor descriptor : journal.getSuccessfulXssDescriptors()) {
                output.write(
                        "<div class='vulnerability'>" +
                            "<div class='page'>" +
                                descriptor.getPageClass().getCanonicalName() +
                            "</div>" +
                            "<div class='input'>" +
                                descriptor.getInputIdentifier() +
                            "</div>" +
                        "</div>"
                );
            }
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
