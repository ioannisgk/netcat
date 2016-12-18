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

namespace TcpChat
{
    public partial class Form1 : Form
    {
        bool running = false;
        delegate void SetTextCallback(string text);
        TcpClient client;
        NetworkStream ns;
        Thread t = null;

        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            String s = textBox5.Text + " > " + textBox2.Text;
            byte[] byteTime = Encoding.ASCII.GetBytes(s);
            ns.Write(byteTime, 0, byteTime.Length);
            if (s.Equals("quit"))
            {
                t.Abort();
                Application.Exit();
            }
        }

        // This is run as a thread
        public void DoWork()
        {
            byte[] bytes = new byte[1024];
            while (true)
            {
                int bytesRead = ns.Read(bytes, 0, bytes.Length);
                this.SetText(Encoding.ASCII.GetString(bytes, 0, bytesRead));
            }
        }
        private void SetText(string text)
        {
            // InvokeRequired required compares the thread ID of the
            // calling thread to the thread ID of the creating thread.
            // If these threads are different, it returns true.
            if (this.textBox1.InvokeRequired)
            {
                SetTextCallback d = new SetTextCallback(SetText);
                this.Invoke(d, new object[] { text });
            }
            else
            {
                this.textBox1.Text = this.textBox1.Text + text + "\r\n";
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (running)
            {
                button2.Text = "Start";
                button1.Enabled = false;
                running = false;
                t.Abort();
            }
            else
            {
                button2.Text = "Stop";
                button1.Enabled = true;
                running = true;

                portNum = int.Parse(textBox4.Text);
                string hostName = textBox3.Text;

                client = new TcpClient(hostName, portNum);
                ns = client.GetStream();
                String s = "Connected";
                byte[] byteTime = Encoding.ASCII.GetBytes(s);
                ns.Write(byteTime, 0, byteTime.Length);
                t = new Thread(DoWork);
                t.Start();
            }
        }
    }
}
