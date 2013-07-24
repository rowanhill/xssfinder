package org.xssfinder.reporting;

import org.xssfinder.remote.PageDefinition;

import java.io.*;

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
            output.write("<script src=\"http://code.jquery.com/jquery-1.10.2.min.js\"></script>");
            output.write("</head>");
            output.write("<body>");

            output.write("<h2>Summary</h2>");
            output.write("<p>There were <span id='summary-vulns'>"+journal.getXssSightings().size()+"</span> ");
            output.write("detected vulnerabilitie(s), ");
            output.write("<span id='summary-untested'>"+journal.getPagesClassWithUntestedInputs().size()+"</span> page(s) with untested inputs, ");
            output.write("and <span id='summary-exceptions'>"+journal.getErrorContexts().size()+"</span> error(s).</p>");

            output.write("<h2>Vulnerabilities</h2>");
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

            output.write("<h2>Untested inputs</h2>");
            output.write("<p>Potentially untested inputs were found on the following pages:</p>");
            output.write("<table id='warnings'>");
            output.write("<tr>");
            output.write("<th>Page Object</th>");
            output.write("</tr>");
            for (PageDefinition pageClass : journal.getPagesClassWithUntestedInputs()) {
                output.write(
                        "<tr>" +
                            "<td>" +
                                pageClass.getIdentifier() +
                            "</td>" +
                        "</tr>"
                );
            }
            output.write("</table>");

            output.write("<h2>Exceptions</h2>");
            output.write("<p>The following exceptions occurred:</p>");
            output.write("<table id='errors'>");
            output.write("<tr>");
            output.write("<th>Page Class</th>");
            output.write("<th>Traversal method</th>");
            output.write("<th>Traversal mode</th>");
            output.write("<th>Details</th>");
            output.write("</tr>");
            for (RouteRunErrorContext errorContext : journal.getErrorContexts()) {
                String rowId = "errorContext-" + errorContext.hashCode();
                String rowDetailsId = "errorContext-details-" + errorContext.hashCode();
                output.write(
                        "<tr id='" + rowId + "'>" +
                            "<td>" +
                                errorContext.getPageIdentifier() +
                            "</td>" +
                            "<td>" +
                                errorContext.getPageTraversalMethodString() +
                            "</td>" +
                            "<td>" +
                                errorContext.getTraversalModeName() +
                            "</td>" +
                            "<td>" +
                                "<a href=\"#\" onClick=\"$('#" + rowDetailsId + "').toggle(); return false;\">Toggle details</a>" +
                            "</td>" +
                        "</tr>" +
                        "<tr id='" + rowDetailsId + "' style='display:none'>" +
                            "<td colspan='4'>" +
                                "<div class='message'>" + errorContext.getExceptionMessage() + "</div>" +
                                "<pre>"
                );
                errorContext.printStackTrace(new PrintWriter(output));
                output.write(
                                "</pre>" +
                            "</td>" +
                        "</tr>"
                );
            }
            output.write("</tr>");
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
