package com.aemlogs.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.aemlogs.services.AemLogsOsgiConfig;

@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/aem-slinglogs",
        ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=txt",
        "sling.auth.requirements=-/bin/aem-slinglogs"
})
public class LogViewerServlet extends SlingSafeMethodsServlet {




    @Override
    protected void service(SlingHttpServletRequest req, SlingHttpServletResponse resp)
            throws ServletException, IOException {
        if (!HttpConstants.METHOD_GET.equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            resp.getWriter().write("Method " + req.getMethod() + " not supported");
            return;
        }
        super.service(req, resp);
    }

    @Reference
    private AemLogsOsgiConfig aemLogsOsgiConfig;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        // Filter GET Request method only
        if (!HttpConstants.METHOD_GET.equals(req.getMethod())) {

            out.println("<html><body>");
            out.println(
                    "<div class=\"error\">Error: This method (" + req.getMethod()
                            + ") is not allowed. Only HTTP GET requests are supported</div>");
            out.println("</body> </html>");
            return;
        }

        // Allowed parameter names
        Set<String> allowedParams = new HashSet<>(Arrays.asList("file", "lines"));

        // Check for any disallowed parameters
        boolean invalidParamsPresent = false;
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!allowedParams.contains(paramName)) {
                invalidParamsPresent = true;
                break;
            }
            if (paramName.equals("lines")) {
                String linesParamValue = req.getParameter("lines");
                try {
                    // Check if the lines parameter is an integer
                    Integer.parseInt(linesParamValue);
                } catch (NumberFormatException e) {
                    // Parameter is provided but not a valid integer, set flag to true
                    invalidParamsPresent = true;
                    break;
                }
            }
        }

        if (invalidParamsPresent) {
            out.println("<html><body>");
            out.println(
                    "<div class=\"error\">Error: Invalid Parameters - Only 'file' and 'line' (with integer value) are allowed.</div>");
            out.println("</body> </html>");
            return;
        }

        // checking for empty basePath coming via osgi
        String basePath = aemLogsOsgiConfig.getFilePath();
        if (basePath != null && basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        
        if (basePath == null || basePath.trim().isEmpty()) {
            out.println("<html><body>");
            out.println("<div class=\"error\">Configuration error: The log file path is not configured.</div>");
            out.println("</body> </html>");
            return;
        }

        // Get the 'file' parameter from the request, default to "error.log" if not
        // provided
        String fileNameParam = req.getParameter("file");
        String logFilePath = basePath + "/"+ (fileNameParam != null ? fileNameParam : "error.log");

        String linesParam = req.getParameter("lines");
        int numberOfLinesToShow = -1; // Default to show all lines if parameter is missing or invalid
        if (linesParam != null) {
            try {
                numberOfLinesToShow = Integer.parseInt(linesParam);
            } catch (NumberFormatException e) {
                // Parameter is provided but not a valid integer, ignore it
            }
        }

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Log Viewer</title>");
        out.println("<style>");
        out.println("body { font-family: monospace; white-space: pre; }");
        out.println(".error { color: #dc3545; }");
        out.println(".warn  { color: #ffc107; }");
        out.println(".info  { color: #0d6efd; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        LinkedList<String> lines = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            out.println("<h2>" + (linesParam != null ? linesParam : "All") + " Lines from "
                    + (fileNameParam != null ? fileNameParam : "error.log") + "</h2>");
            String line;
            while ((line = reader.readLine()) != null) {
                if (numberOfLinesToShow > 0) {
                    if (lines.size() == numberOfLinesToShow) {
                        lines.removeFirst();
                    }
                    lines.addLast(line);
                } else {
                    // If numberOfLinesToShow is -1 (default), read all lines without removing any
                    // from the list
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            out.println("<div>Error : Invalid file name" + "</div>");
            return;
        }

        for (String logLine : lines) {
            if (logLine.contains("ERROR")) {
                out.println("<div class=\"error\">" + logLine + "</div>");
            } else if (logLine.contains("WARN")) {
                out.println("<div class=\"warn\">" + logLine + "</div>");
            } else if (logLine.contains("INFO")) {
                out.println("<div class=\"info\">" + logLine + "</div>");
            } else {
                out.println(logLine + "<br>");
            }
        }

        out.println("</body>");
        out.println("</html>");
    }
}
