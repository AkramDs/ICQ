import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;                
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;

public class ICQ extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTextField ipAddress;
    private JTextArea MessageText;
    private static JTextArea receiveMessageText;

    public ICQ() {
        ipAddress = new JTextField("localhost");
        ipAddress.setBounds(30, 25, 440, 40);

        MessageText = new JTextArea("Message");
        JScrollPane scrollPane = new JScrollPane(MessageText);
        scrollPane.setBounds(30, 75, 440, 200);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(370, 285, 100, 35);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Thread sendThread = new Thread(send());
                sendThread.start();

            }
        });

        receiveMessageText = new JTextArea("Receive Text \n\n");
        receiveMessageText.setEditable(false);

        JScrollPane scrollPaneReceive = new JScrollPane(receiveMessageText);
        scrollPaneReceive.setBounds(30, 330, 440, 200);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(370, 540, 100, 35);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                receiveMessageText.setText("");
            }
        });

        setLayout(null);
        add(ipAddress);
        add(scrollPane);
        add(sendButton);
        add(scrollPaneReceive);
        add(clearButton);

    }

    private static void createAndShowGUI() {
        ICQ newContentPane = new ICQ();
        newContentPane.setOpaque(true);

        JFrame frame = new JFrame("ICQ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setResizable(false);
        frame.setLocation(100, 20);
        frame.setContentPane(newContentPane);
        frame.setVisible(true);

    }

    private static Runnable server() {
        int port = 5005;

        while (true) {
            try (ServerSocket ssk = new ServerSocket(port)) {

                Socket sk = ssk.accept();

                InputStream is = sk.getInputStream();
                ;

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                int charracter;
                String message = "";

                for (int i = 0; i < 1024; i++) {
                    charracter = br.read();
                    // System.out.print((char) charracter);
                    if(charracter != -1) {
                        message += ((char) charracter);
                    }
                }

                String total = sk.getRemoteSocketAddress().toString() + ": " + message;

                receiveMessageText.append(total);
                System.out.println(total);

                br.close();
                sk.close();
                ssk.close();

            } catch (IOException ioe) {
                System.out.println(ioe);
                return null;

            }
        }
    }

    private Runnable send() {
        String ip = ipAddress.getText();
        int port = 5005;

        try (Socket sk = new Socket(ip, port)) {

            OutputStream os = sk.getOutputStream();

            PrintWriter pw = new PrintWriter(os, true);
            pw.println(MessageText.getText());

            // System.out.println("\n send \n");

            sk.close();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());

        }

        return null;
    }

    private static Thread startServer() {
        new Thread(server()).start();

        return null;
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        startServer();
    }
} 