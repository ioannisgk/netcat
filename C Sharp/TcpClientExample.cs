using System;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace TcpClientExample
{
    class MainClass
    {
        public static void Main(string[] args)
        {
            TcpClient client  = new TcpClient("194.81.104.141", 4321);
            StreamWriter sw = new StreamWriter(client.GetStream());

            sw.WriteLine("Hello TCP Server");
            sw.AutoFlush = true;

            client.Close ();
        }
    }
}
