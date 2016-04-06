package parser;

import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Start {

    public static void main(String [] args) throws IOException {
        
        PrintWriter writer = new PrintWriter("failedReport.html", "UTF-8");
        writer.println("<html><head><title></title></head><body><table>");
        String reportPathPattern = "http://jenkins.marks.kyiv.epam.com/view/TAF/view/TAF_POS/job/%s/thucydides";
        
        //for manual run change to "<jobName>/<build number>"
        String jobName = "08.2.2_run_test_suite";
        if (0 != args.length) {
            jobName = args[0];
        }
        if (jobName.contains("/")) {
            reportPathPattern += "Report";
        }
        String reportPath = String.format(reportPathPattern, jobName);
        System.out.println(reportPath); // NOSONAR
        Document doc = Jsoup.connect(reportPath).timeout(60000).maxBodySize(1024 * 1024 * 1024).get();
        Elements errorList = doc.select(".test-ERROR > .ERROR-text, .test-FAILURE > .FAILURE-text");

        errorList.stream().forEach(story -> {
            System.out.println(story.text() + "\t" + getErrorDescription(story)); // NOSONAR
            writer.println("<tr><td>"+story.text() + "</td><td>" + getErrorDescription(story)+"</td></tr>");
            
        }
        );
        
        writer.println("</table></body></html>");
        writer.close();
        
    }
    
    public static String getErrorDescription (Element element) {
     // split() used for removing additional error description
        return element.select("a").get(0).attr("title").split("For documentation on this error")[0]
                .split("Build info:")[0].split("\\(Session info:")[0];
    }
    
}
