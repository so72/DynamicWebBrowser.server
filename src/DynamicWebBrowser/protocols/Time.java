/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicWebBrowser.protocols;

import java.net.URI;
import java.util.Date;

/**
 *
 * @author steffen
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
