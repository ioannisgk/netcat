import java.io.*;
import java.net.*;

public class TcpClient
{
    public static void main(String[] args) throws IOException
    {
     Socket socket = new Socket ("194.81.104.234", 2345);
     PrintWriter out = new PrintWriter (socket.getOutputStream(), true);

     out.println ("Hello from JAVA TCP Client");

     out.close ();
     socket.close ();
    }
}
