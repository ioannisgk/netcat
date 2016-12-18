using System;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace UdpServerExample
{
    class MainClass
    {
        public static void Main (string[] args)
        {
            UdpClient client = new UdpClient (4567);
            IPEndPoint RemoteIpEndPoint = new IPEndPoint (IPAddress.Any, 0);
            String fromServer = Encoding.ASCII.GetString (client.Receive (ref RemoteIpEndPoint));
            Console.WriteLine (fromServer);
        }
    }
}
