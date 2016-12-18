import java.io.*;
import java.net.*;

public class TcpServer
{
    public static void main (String[] args) throws IOException
    {
     ServerSocket serverSocket = new ServerSocket (2345);
     Socket clientSocket = serverSocket.accept ();
     BufferedReader in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));

     String fromClient = in.readLine ();
     System.out.println ("Client Message: " + fromClient);

     in.close ();
     clientSocket.close ();
     serverSocket.close ();
    }
}
