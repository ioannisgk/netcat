using System;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace UdpClientExample
{
    class MainClass
    {
        public static void Main(string[] args)
        {
            UdpClient client = new UdpClient();
            String toServer = "Hello UDP Server\n";
            client.Send(Encoding.ASCII.GetBytes(toServer), toServer.Length, "194.81.104.123", 4567);
            client.Close();
        }
    }
}
