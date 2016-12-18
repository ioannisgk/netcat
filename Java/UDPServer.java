import java.net.*;
import java.io.*;

public class UDPServer
{
    public static void main (String[] args)
    {
        try
        {
            DatagramSocket socket = new DatagramSocket (4567);
            DatagramPacket packet;
            byte[] buf = new byte[256];

            packet = new DatagramPacket (buf, buf.length);
            socket.receive (packet);
            String received = new String (packet.getData());
            System.out.println ("Received packet: " + received);
        }
        catch (IOException e)
        {
        }
    }
}
