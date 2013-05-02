package DynamicWebBrowser.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class ClassServer implements Runnable {

    private final String PROP_FILE = "ClassServer.properties";
    private ServerSocket serverSocket;
    private Properties properties;
    private String documentRoot;
    private int port;

    public static void main(String[] args) {
        (new Thread(new ClassServer())).start();
    }

    public ClassServer() {
        properties = new Properties();

        try {
            //load a properties file
            properties.load(this.getClass().getResourceAsStream(PROP_FILE));
        } catch (IOException ex) {
            System.err.println("Failed to open properties file.");
        }

        documentRoot = properties.getProperty("root");
        port = Integer.parseInt(properties.getProperty("port"));
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                System.out.println("\nWaiting for connection on port: " + port);
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ConnectionHandler(socket));
                System.out.println("\nCreating Thread(" + thread.getId() + ") for incoming connection: ");
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This thread processes a client (web browser) request. In the meantime the
     * web server can accept other clients.
     */
    class ConnectionHandler implements Runnable {

        Socket socket = null;
        BufferedReader readFromNet = null;
        PrintStream writeToNet = null;
        String inputLine;
        String httpMethod;
        StringTokenizer tokenizer;
        String fileString;
        String version;
        String contentType;
        File fileToServe;
        
        private long threadID;

        ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * The method
         * <code>run()</code> is the core of the server
         */
        @Override
        public void run() {
            threadID = Thread.currentThread().getId();
            log("Connection established.");
            try {
                writeToNet = new PrintStream(socket.getOutputStream());
                readFromNet = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                inputLine = readFromNet.readLine();
                
                tokenizer = new StringTokenizer(inputLine);
                httpMethod = tokenizer.nextToken();

                if (httpMethod.equals("CLASS")) {
                    String protocol = tokenizer.nextToken();
                    
                    log("Client asked for: " + protocol);
                    
                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }
                    
                    // Skip the rest
                    while ((inputLine = readFromNet.readLine()) != null) {
                        if (inputLine.trim().equals("")) {
                            break;
                        }
                    }
                    
                    String classFile = properties.getProperty(protocol);
                    if (classFile != null) {
                        // Protocol is known
                        if (version.startsWith("HTTP/")) {
                            // Send a MIME header
                            writeToNet.print("HTTP/1.0 200 OK\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-length: " + classFile.length() + "\r\n");
                            writeToNet.print("Content-type: text/plain\r\n\r\n");
                        }
                        
                        writeToNet.print(classFile);
                        writeToNet.close();
                        log("Sent protocol class name.");
                    } else {
                        // Server doesn't know this protocol
                        if (version.startsWith("HTTP/")) {
                            // send a MIME header
                            writeToNet.print("HTTP/1.0 501 Not Implemented\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                        }

                        writeToNet.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                        writeToNet.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                        writeToNet.close();
                        
                        System.err.println("Class was not known, sent 501");
                    }
                } else if (httpMethod.equals("GET")) {
                    fileString = tokenizer.nextToken();

                    contentType = guessContentTypeFromName(fileString);

                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }

                    // Skip the rest
                    while ((inputLine = readFromNet.readLine()) != null) {
                        if (inputLine.trim().equals("")) {
                            break;
                        }
                    }

                    try {
                        log("FileString: " + "\"" + fileString + "\"");
                        fileToServe = new File(documentRoot, fileString);
                        FileInputStream fis = new FileInputStream(fileToServe);
                        byte[] theData = new byte[(int) fileToServe.length()];
                        
                        fis.read(theData);
                        fis.close();

                        if (version.startsWith("HTTP/")) {
                            // Send a MIME header
                            writeToNet.print("HTTP/1.0 200 OK\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-length: " + theData.length + "\r\n");
                            writeToNet.print("Content-type: " + contentType + "\r\n\r\n");
                        }

                        // Send the file
                        writeToNet.write(theData);
                        writeToNet.close();
                        log("File: " + fileToServe + " sent\n");

                    } catch (IOException e) {
                        // Cannot find the file
                        if (version.startsWith("HTTP/")) {
                            // send a MIME header
                            writeToNet.print("HTTP/1.0 404 File Not Found\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                        }
                        writeToNet.println("<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD>");
                        writeToNet.println("<BODY><H1>HTTP Error 404: File Not Found</H1></BODY></HTML>");
                        writeToNet.close();
                        log("File: " + fileToServe + " not found\n");
                    }
                } else if (httpMethod.equals("UPLOAD")) {
                    boolean succeeded = false;
                    String sourceFileName = tokenizer.nextToken();

                    log("User is attempting to upload file: " + sourceFileName);

                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }

                    String headerLine = readFromNet.readLine();
                    tokenizer = new StringTokenizer(headerLine);

                    if (!tokenizer.nextToken().equals("protocol:")) {
                        // TODO: deal with invalid UPLOAD request
                    }

                    String protocol = tokenizer.nextToken();

                    log("protocol is: " + protocol);

                    while(!readFromNet.readLine().equals(""));

                    // The remainder should be the file contents.
                    String fileContents = "";
                    String line;
                    while ((line = readFromNet.readLine()) != null) {
                        fileContents += line + "\n";
                    }
                    try{
                        File sourceFile = new File(documentRoot, sourceFileName);
                        FileOutputStream fos = new FileOutputStream(sourceFile);

                        fos.write(fileContents.getBytes());
                    } catch (IOException e) {
                        log("Failed writing to file");
                        succeeded = false;
                    }

                    // Compile the new source
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

                    if (compiler == null) {
                        log("Compiler could not be found");
                        succeeded = false;
                    } else {
                        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                        Iterable<? extends JavaFileObject> fileObject = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(documentRoot + sourceFileName));
                        Iterable<String> options = Arrays.asList(new String[]{"-Xlint:deprecation"});

                        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, fileObject);

                        log("Compiling...");
                        if (!task.call()) {
                            log("Compilation failed!");
                            succeeded = false;
                        } else {
                            log("Sucessfully compiled protocol");
                            succeeded = true;
                        }
                    }

                    if (succeeded) {
                        tokenizer = new StringTokenizer(sourceFileName, ".");
                        String className = tokenizer.nextToken() + ".class";

                        // TODO: may want to synchronize this call? Not sure.
                        properties.setProperty(protocol, className);
                        
                        writeToNet.print("HTTP/1.0 200 OK\r\n\r\n");
                        writeToNet.close();
                    } else {
                        writeToNet.print("HTTP/1.0 500 Internal Service Error\r\n\r\n");
                        writeToNet.close();
                    }

                } else {
                    // Method doesn't equal "GET"
                    if (version.startsWith("HTTP/")) {
                        // send a MIME header
                        writeToNet.print("HTTP/1.0 501 Not Implemented\r\n");
                        writeToNet.print("Date: " + new Date() + "\r\n");
                        writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                        writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                    }

                    writeToNet.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                    writeToNet.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                    writeToNet.close();

                    log("Method: " + httpMethod + " is not supported\n");

                }
            } catch (IOException e) {
            }

            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        
        private void log(String message) {
            System.out.print("\n" + threadID + ": " + message + "\n");
        }

        /**
         * The method
         * <code>guessContentTypeFromName()</code> returns the MIME-type of a
         * file, which is guessed from the file's extention.
         */
        public String guessContentTypeFromName(String name) {
            if (name.endsWith(".html") || name.endsWith(".htm")) {
                return "text/html";
            } else if (name.endsWith(".txt") || name.endsWith(".java")) {
                return "text/plain";
            } else if (name.endsWith(".gif")) {
                return "image/gif";
            } else if (name.endsWith(".class")) {
                return "application/octet-stream";
            } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "text/plain";
            }
        }
    }
}
