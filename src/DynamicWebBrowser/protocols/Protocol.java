package DynamicWebBrowser.protocols;

import java.net.URI;

/**
 *
 * @author Steffen, Mark, Shane
 */
public interface Protocol {
    
    /**
     * If this method signature is changed,
     * Make sure to also change it in the browser
     * project.
     * 
     * @param   uri the URI from the address bar
     * @return      the html string for the browser to display
     */
    public String execute(URI uri);
}
