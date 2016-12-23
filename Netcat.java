import java.awt.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

class NetcatGUI extends JFrame
{
    public JButton sendButton;
    public JTextArea rxArea, txArea;
    public JScrollPane pane;
    public Container container;

    public NetcatGUI (String title)
    {
        super (title);

        container = getContentPane ();
        container.setLayout (new FlowLayout());

        txArea = new JTextArea (6, 40);
        rxArea = new JTextArea (6, 40);
        rxArea.setEditable (false);
        pane = new JScrollPane (rxArea);

        sendButton = new JButton ("Send");

        container.add (pane);
        container.add (txArea);
        container.add (sendButton);
    }
}

public class Netcat extends NetcatGUI
{
    String inputLine, outputLine;
    String role;
    static String remoteAddr;
    static String localPort, remotePort;

    class NetcatStartupGUI extends JFrame
    {
        public JButton startButton;
        JComboBox netcatRole;
        public JTextField remotePortTextField, remoteIPTextField, localPortTextField;
        public Container container;

        public NetcatStartupGUI (String title)
        {
            super (title);
            String[] netcatRoleString = {"                                              Netcat Role:                                         ",
            "TCP Server", "TCP Client", "UDP Server", "UDP Client"};

            container = getContentPane ();
            container.setLayout (new FlowLayout());
            netcatRole = new JComboBox(netcatRoleString);
            remotePortTextField = new JTextField (40);
            remoteIPTextField = new JTextField (40);
            localPortTextField = new JTextField (40);

            startButton = new JButton ("Start");

            container.add (new JLabel ("                                                       "));
            container.add (netcatRole);
            container.add (new JLabel ("Remote IP:"));
            container.add (remoteIPTextField);
            container.add (new JLabel ("Remote Port:"));
            container.add (remotePortTextField);
            container.add (new JLabel ("Local Port:"));
            container.add (localPortTextField);
            container.add (startButton);
        }
    }

    class NetcatStartup extends NetcatStartupGUI
    {
        public ButtonHandler bHandler;
        volatile boolean finished = false;

        public NetcatStartup (String title)
        {
            super (title);
            bHandler = new ButtonHandler ();
            startButton.addActionListener (bHandler);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event)
            {
                role = (String)netcatRole.getSelectedItem();
                remoteAddr = remoteIPTextField.getText ();
                remotePort = remotePortTextField.getText ();
                localPort = localPortTextField.getText ();
                finished = true;
            }
        }

        public boolean run ()
        {
            while (!finished);
            return true;
        }
    }

    public Netcat (String title) throws IOException
    {
        super (title);
    }

    class TcpServer
    {
        ServerSocket serverSocket;
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        ButtonHandler txButtonHandler;

        TcpServer (String port) throws IOException
        {
            // Create a ServerSocket for listening to a client's messages
            // out is used to send messages back to client
            // in is used to get messages from client
            serverSocket = new ServerSocket (Integer.parseInt (port));
            socket = serverSocket.accept ();
            out = new PrintWriter (socket.getOutputStream(), true);
            in = new BufferedReader (new InputStreamReader (socket.getInputStream ()));

            txButtonHandler = new ButtonHandler ();
            sendButton.addActionListener (txButtonHandler);

            rx ();

            socket.close ();
            serverSocket.close ();

            System.exit (1);
        }

        void tx () throws IOException
        {
            // Get text from txArea and send it to client
            // Print to console and send to client
            String outputLine = txArea.getText ();
            System.out.println ("Sending to TCP Client > " + outputLine);
            out.println (outputLine);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event) //throws IOException
            {
                try{tx ();} catch (IOException e){}
            }
        }

        void rx () throws IOException
        {
            String received;
            do
            {
                // When a line is received and is not null, print it to rxArea
                received = in.readLine ();
                if (received != null) rxArea.setText (rxArea.getText () + "\n" + received);
            }
            while (received != null);
        }
    }

    class TcpClient
    {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        ButtonHandler txButtonHandler;

        TcpClient (String address, String port) throws IOException
        {
            // Create a Socket to send messages to server
            // out is used to send messages back to server
            // in is used to get messages from server
            socket = new Socket (address, Integer.parseInt (port));
            out = new PrintWriter (socket.getOutputStream(), true);
            in = new BufferedReader (new InputStreamReader (socket.getInputStream ()));

            txButtonHandler = new ButtonHandler ();
            sendButton.addActionListener (txButtonHandler);

            rx ();

            socket.close ();

            System.exit (1);
        }

        void tx () throws IOException
        {
            // Get text from txArea and send it to server
            // Print to console and send to server
            String outputLine = txArea.getText ();
            System.out.println ("Sending to TCP Server > " + outputLine);
            out.println (outputLine);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event) //throws IOException
            {
                try{tx ();} catch (IOException e){}
            }
        }

        void rx () throws IOException
        {
            String received;
            do
            {
                // When a line is received and is not null, print it to rxArea
                received = in.readLine ();
                if (received != null) rxArea.setText (rxArea.getText () + "\n" + received);
            }
            while (received != null);
        }
    }

    class UdpServer
    {
        DatagramSocket socket;
        DatagramPacket packet;
        byte[] buf;
        InetAddress address;
        ButtonHandler txButtonHandler;

        UdpServer (String port) throws IOException
        {
            // Create a DatagramSocket for listening to a client's messages
            // buf is used to send and get DatagramPackets from client
            socket = new DatagramSocket (Integer.parseInt (port));
            buf = new byte[256];

            txButtonHandler = new ButtonHandler ();
            sendButton.addActionListener (txButtonHandler);

            rx ();

            System.exit (1);
        }

        void tx () throws IOException
        {
            // Get text from txArea and put in in buffer
            // Get InetAddress and remote port and send data to client
            String outputLine = txArea.getText ();
            //byte[] buf = new byte[256];
            buf = outputLine.getBytes ();
            address = InetAddress.getByName(remoteAddr);

            packet = new DatagramPacket (buf, buf.length, address, Integer.parseInt (remotePort));
            System.out.println ("Sending to UDP Client > " + outputLine);
            socket.send (packet);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event) //throws IOException
            {
                try{tx ();} catch (IOException e){}
            }
        }

        void rx () throws IOException
        {
            String received;

            do
            {
                // When a line is received and is not null,
                // receive DatagramPacket from client,
                // convert to String and print it to rxArea
                packet = new DatagramPacket (buf, buf.length);
                socket.receive (packet);
                received = new String (packet.getData());
                rxArea.setText (rxArea.getText () + "\n" + received);
            }
            while (received != null);
        }
    }

    class UdpClient
    {
        DatagramSocket socket;
        DatagramPacket packet;
        byte[] buf;
        InetAddress address;
        ButtonHandler txButtonHandler;

        UdpClient (String address, String port) throws IOException
        {
            // Create a DatagramSocket for sending messages to server
            // buf is used to get and send DatagramPackets to server
            socket = new DatagramSocket ();
            buf = new byte[256];

            txButtonHandler = new ButtonHandler ();
            sendButton.addActionListener (txButtonHandler);

            rx ();

            System.exit (1);
        }

        void tx () throws IOException
        {
            // Get text from txArea and put in in buffer
            // Get InetAddress and remote port and send data to server
            String outputLine = txArea.getText ();
            buf = outputLine.getBytes ();
            address = InetAddress.getByName (remoteAddr);

            packet = new DatagramPacket (buf, buf.length, address, Integer.parseInt (remotePort));
            System.out.println ("Sending to UDP Server > " + outputLine);
            socket.send (packet);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event) //throws IOException
            {
                try{tx ();} catch (IOException e){}
            }
        }

        void rx () throws IOException
        {
            String received;

            do
            {
                // When a line is received and is not null,
                // receive DatagramPacket from server,
                // convert to String and print it to rxArea
                packet = new DatagramPacket (buf, buf.length);
                socket.receive (packet);
                received = new String (packet.getData());
                rxArea.setText (rxArea.getText () + "\n" + received);
            }
            while (received != null);
        }
    }


    public void run () throws IOException
    {
        if (role.equals ("TCP Server")) {
          System.out.println ("nc -l " + localPort);
          new TcpServer (localPort);
        }
        if (role.equals ("TCP Client")) {
          System.out.println ("nc " + remoteAddr + " " + remotePort);
          new TcpClient (remoteAddr, remotePort);
        }
        if (role.equals ("UDP Server")) {
          System.out.println ("nc -u -l " + localPort);
          new UdpServer (localPort);
        }
        if (role.equals ("UDP Client")) {
          System.out.println ("nc -u " + remoteAddr + " " + remotePort);
          new UdpClient (remoteAddr, remotePort);
        }
    }

    public void g ()
    {
        NetcatStartup p = new NetcatStartup ("Netcat Connection");

        p.setSize(new Dimension (550,280));
        p.setVisible (true);
        p.run ();
        p.dispose ();
    }

    public static void main (String[] args) throws IOException
    {
        Netcat f = new Netcat ("Netcat");

        f.g();

        f.setSize(new Dimension (550,280));
        f.setVisible (true);
        f.run ();
    }
}
