package DynamicWebBrowser.protocols;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class Http implements Protocol {

    private Socket server;
    private String host;
    private int port;
    private PrintStream writer;
    private DataInputStream inputStream;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;

    public Http(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Loads the class from server
     *
     * @param protocolName
     * @return null if protocol doesn't exist
     */
    public String execute(URI uri) {

        URL ur = null;
        try {
            ur = new URL(uri.getAuthority());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }
        URLConnection conn = null;
        try {
            conn = ur.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream is = null;
        try {
            is = conn.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
        }
        String foo = new Scanner(is).useDelimiter("\\A").next();
        System.out.println(foo);
        return foo;
    }
}
