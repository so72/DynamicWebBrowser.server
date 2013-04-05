package DynamicWebBrowser.protocols;

import java.net.URI;
import java.util.Date;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class Time implements Protocol {

    @Override
    public String execute(URI uri) {
        String response = "";
        response += "<HTML><HEAD><TITLE>Date</TITLE></HEAD>\n";
        response += "<BODY><H1>" + new Date() + "</H1></BODY></HTML>";
        return response;
    }
}
