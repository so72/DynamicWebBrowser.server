package DynamicWebBrowser.protocols;

import java.net.URI;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class WFB implements Protocol {

	@Override
	public String execute(URI uri) {
		// TODO: implement this method
		String response = "";
        response += "<HTML><HEAD><TITLE>Date</TITLE></HEAD>\n";
        response += "<BODY><H1>World Fact Book is not implemented yet.</H1></BODY></HTML>";
        return response;
	}
}