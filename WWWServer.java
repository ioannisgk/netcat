import java.io.*;
import java.net.*;
import java.util.*;

public class WWWServer
{
    static private String format="\n\n<html><h1><font color=\"red\">HelloWorld</font></h1></html>\n";

    public static void main (String[] args) throws IOException
    {
        ServerSocket listenSocket = new ServerSocket (17002);
        Socket clientSocket = listenSocket.accept ();

        PrintStream out = new PrintStream (clientSocket.getOutputStream ());
        BufferedReader in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));

        System.out.println ("From Browser: " + in.readLine ());
        out.printf (format);

        clientSocket.close ();
        listenSocket.close ();
    }
}


//www.eng.northampton.ac.uk 80

//GET /~espen/ejs.html HTTP/1.1
//Host 127.0.0.1:1234
