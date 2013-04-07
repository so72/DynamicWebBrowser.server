package DynamicWebBrowser.protocols;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
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

    /**
     * Loads the class from server
     *
     * @param protocolName
     * @return null if protocol doesn't exist
     */
//    public String execute(URI uri) {
//
//        URL ur = null;
//        try {
//            ur = new URL(uri.getScheme(), uri.getAuthority(), 80, uri.getPath());
//        } catch (MalformedURLException ex) {
//            System.out.println("the uri is bad right before trying to execute");
//            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        URLConnection conn = null;
//        try {
//            conn = ur.openConnection();
//        } catch (IOException ex) {
//            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        InputStream is = null;
//        try {
//            is = conn.getInputStream();
//        } catch (IOException ex) {
//            Logger.getLogger(Http.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        String foo = new Scanner(is).useDelimiter("\\A").next();
//        System.out.println(foo);
//        return foo;
//    }
//    
    public String execute(URI uri) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = uri.toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
