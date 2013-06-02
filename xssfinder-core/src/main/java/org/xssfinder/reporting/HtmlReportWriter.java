package org.xssfinder.reporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes HTML reports of the results of a run.
 */
public class HtmlReportWriter {
    private final String outFilePath;

    /**
     * @param outFilePath The path to write the report to
     */
    public HtmlReportWriter(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    /**
     * Write the given XssJournal out to the writer's out file path
     *
     * @param journal An XssJournal detailing the results of a run
     * @throws IOException Thrown if there
     */
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

            output.write("<p>Potentially untested inputs were found on the following pages:</p>");
            output.write("<table id='warnings'>");
            output.write("<tr>");
            output.write("<th>Page Object</th>");
            output.write("</tr>");
            for (Class<?> pageClass : journal.getPagesClassWithUntestedInputs()) {
                output.write(
                        "<tr>" +
                            "<td>" +
                                pageClass.getCanonicalName() +
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
        File outFile = new File(outFilePath).getAbsoluteFile();
        outFile.createNewFile();
        return outFile;
    }
}
