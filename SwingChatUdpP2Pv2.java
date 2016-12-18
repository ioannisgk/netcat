import java.awt.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

class SwingChatGUI extends JFrame
{
    public JButton sendButton;
    public JTextArea rxArea;
    public JTextField txArea;
    public JScrollPane pane;
    public Container container;
    String messages = "<Started>";

    public SwingChatGUI (String title)
    {
        super (title);

        container = getContentPane ();
        container.setLayout (new FlowLayout());

        txArea = new JTextField (40);
        rxArea = new JTextArea (6, 40);
        rxArea.setEditable (false);
        pane = new JScrollPane (rxArea);

        sendButton = new JButton ("Send");

        container.add (pane);
        container.add (txArea);
        container.add (sendButton);
    }
}

public class SwingChatUdpP2Pv2 extends SwingChatGUI
{
    String inputLine, outputLine;
    static String remoteAddr;
    static int localPort, remotePort; ;
    public ButtonHandler bHandler;
    DatagramSocket socket;

    class SwingPasswordGUI extends JFrame
    {
        public JButton startButton;
        public JTextField remotePortTextField, remoteIPTextField, localPortTextField;
        public Container container;

        public SwingPasswordGUI (String title)
        {
            super (title);

            container = getContentPane ();
            container.setLayout (new FlowLayout());

            remotePortTextField = new JTextField (40);
            remoteIPTextField = new JTextField (40);
            localPortTextField = new JTextField (40);

            startButton = new JButton ("Start");

            container.add (new JLabel ("Remote IP:"));
            container.add (remoteIPTextField);
            container.add (new JLabel ("Remote Port:"));
            container.add (remotePortTextField);
            container.add (new JLabel ("Local IP:"));
            container.add (localPortTextField);
            container.add (startButton);
        }

    }

    class SwingPassword extends SwingPasswordGUI
    {
        public ButtonHandler bHandler;
        volatile boolean finished = false;

        public SwingPassword (String title)
        {
            super (title);
            bHandler = new ButtonHandler ();
            startButton.addActionListener (bHandler);
        }

        private class ButtonHandler implements ActionListener
        {
            public void actionPerformed (ActionEvent event)
            {
                remoteAddr = remoteIPTextField.getText ();
                remotePort = Integer.parseInt (remotePortTextField.getText ());
                localPort = Integer.parseInt (localPortTextField.getText ());

                finished = true;
            }
        }

        public boolean run ()
        {
            while (!finished);
            return true;
        }
    }

    public SwingChatUdpP2Pv2 (String title) throws IOException
    {
     super (title);
     bHandler = new ButtonHandler ();
     sendButton.addActionListener (bHandler);
    }

    // Transmit Message
    private class ButtonHandler implements ActionListener
    {
     public void actionPerformed (ActionEvent event) //throws IOException
     {
          try
          {
               DatagramSocket socket = new DatagramSocket ();
               byte[] buf = new byte[256];

               String outputLine = txArea.getText ();

               buf = outputLine.getBytes ();
               InetAddress address = InetAddress.getByName (remoteAddr);
               DatagramPacket packet = new DatagramPacket (buf, buf.length, address, remotePort);
               System.out.println ("Tx:" + address + ":" + remotePort + ":" + outputLine);
               socket.send (packet);
               System.out.println ("Sent message");
          }
          catch (IOException e)
          {
          }
     }
    }

    // Receive Message
    public void run () throws IOException
    {
        try
        {
            socket = new DatagramSocket (localPort);
            DatagramPacket packet;
            byte[] buf = new byte[256];

            while (true)
            {
                packet = new DatagramPacket (buf, buf.length);
                socket.receive (packet);
                String received = new String (packet.getData());
                System.out.println ("Rx:" + localPort + ":" + received);
                rxArea.setText (received);
            }
        }
        catch (IOException e)
        {
        }
    }


    public void g ()
    {
        SwingPassword p = new SwingPassword ("Start");
        p.setSize(new Dimension (550,250));
        p.setVisible (true);
        p.run ();
        p.dispose ();

    }

    public static void main (String[] args) throws IOException
    {
     SwingChatUdpP2Pv2 f = new SwingChatUdpP2Pv2 ("Chat UDP P2P Program");

     f.g();
     f.setSize(new Dimension (550,200));
     f.setVisible (true);

     try
     {

          f.run ();
     }
     catch (IOException e)
     {
          System.err.println ("Couldn't get I/O");
          System.exit (1);
     }
    }
}
