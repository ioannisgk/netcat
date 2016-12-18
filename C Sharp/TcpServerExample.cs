using System;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace TcpServerExample
{
    class MainClass
    {
        public static void Main(string[] args)
        {
            TcpListener listener = new TcpListener (IPAddress.Parse("127.0.0.1"), 4321);
            listener.Start();
            TcpClient client = listener.AcceptTcpClient();
            StreamReader sr = new StreamReader(client.GetStream());

            Console.WriteLine (sr.ReadLine ());

            client.Close ();
        }
    }
}
