using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace UdpChatP2P
{
    public partial class Form1 : Form
    {
        Thread t = null;
        delegate void SetTextCallback(string text);
        bool running = false;
        UdpClient receiveSocket;

        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Socket sendSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
            IPAddress clientIPAddress = IPAddress.Parse(textBox3.Text);
            int clientPort = int.Parse (textBox4.Text);

            IPEndPoint clientEP = new IPEndPoint(clientIPAddress, clientPort);
            String s = textBox2.Text;
            byte[] message = Encoding.ASCII.GetBytes(s);
            sendSocket.SendTo(message, clientEP);
        }

        public void DoWork()
        {
            int serverPort = int.Parse(textBox5.Text);
            receiveSocket = new UdpClient(serverPort);
            IPEndPoint clientEP = new IPEndPoint(IPAddress.Any, serverPort);

            while (true)
            {
                Console.Write("Waiting for message: ");
                byte[] bytes = receiveSocket.Receive(ref clientEP);
                this.SetText(Encoding.ASCII.GetString(bytes, 0, bytes.Length));
            }
        }

        private void SetText(string text)
        {
            if (this.textBox1.InvokeRequired)
            {
                SetTextCallback d = new SetTextCallback(SetText);
                this.Invoke(d, new object[] { text });
            }
            else
            {
                this.textBox1.Text = this.textBox1.Text + text;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (running)
            {
                button2.Text = "Start";
                button1.Enabled = false;
                running = false;
                receiveSocket.Close();
                t.Abort();
            }
            else
            {
                button2.Text = "Stop";
                button1.Enabled = true;
                running = true;
                t = new Thread(DoWork);
                t.Start();
            }
        }
    }
}
