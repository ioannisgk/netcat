import java.net.*;
import java.io.*;

public class UDPClient
{
    public static void main (String[] args)
    {
        try
        {
            DatagramSocket socket = new DatagramSocket ();
            byte[] buf = new byte[256];
            String messg = "Hello UDP Server\n";

            buf = messg.getBytes ();
            InetAddress address = InetAddress.getByName ("194.81.104.123");
            DatagramPacket packet = new DatagramPacket (buf, buf.length, address, 4567);
            socket.send(packet);
        }
        catch (IOException e)
        {
        }
    }
}
