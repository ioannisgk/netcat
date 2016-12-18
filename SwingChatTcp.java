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

public class SwingChatTcp extends SwingChatGUI
{
    String inputLine, outputLine;
    static String remoteAddr, username;
    static int remotePort;
    PrintWriter out = null;
    public ButtonHandler bHandler;

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
            container.add (new JLabel ("Username:"));
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
                username = localPortTextField.getText ();

                finished = true;
            }
        }

        public boolean run ()
        {
            while (!finished);
            return true;
        }
    }

    public SwingChatTcp (String title) throws IOException
    {
     super (title);
     bHandler = new ButtonHandler ();
     sendButton.addActionListener (bHandler);
    }

    // Transmit Message
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            String outputLine = username + " > " + txArea.getText ();
            System.out.println ("Client > " + outputLine);
            out.println (outputLine);
        }
    }

    // Receive Message
    public void run () throws IOException
    {
        Socket socket = new Socket (remoteAddr, remotePort);
        BufferedReader in = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
        out = new PrintWriter (socket.getOutputStream (), true);

        String inputLine;

        while ((inputLine = in.readLine ()) != null)
        {
            System.out.println ("Client < " + inputLine);
            rxArea.setText (rxArea.getText () + "\n" + inputLine);
        }

        out.close();
        in.close();
        socket.close();
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
     SwingChatTcp f = new SwingChatTcp ("Chat Tcp Client");

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
