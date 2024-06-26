import java.awt.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.*;
import java.io.*;

public class ClientUI extends JFrame {
    private Socket socket;
    private Client client;

    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel lblTargetIP, lblTargetPort, lblUserName, lblYourAddress, lblMessages;
    static JLabel lblServerStatus;
    private JTextField txtFieldTargetIP, txtFieldTargetPort, txtFieldUsername;
    static JTextField txtFieldMessageBox;
    static JButton btnConnect;
    private JButton btnClear;
    static JButton btnSend;

    private static JTextArea txtAreaMessage;
    private JScrollPane scroll;

    private GridBagConstraints setGBC(int x, int y, int px, int py, int gWidth, int gHeight, int to, int le, int bo,
            int ri) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.ipadx = px;
        gbc.ipady = py;
        gbc.gridwidth = gWidth;
        gbc.gridheight = gHeight;
        gbc.insets.set(to, le, bo, ri);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public ClientUI() {
        jFrame = new JFrame("Chat Client");
        jFrame.setSize(1100, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());

        // Create components
        lblTargetIP = new JLabel("Target IP:");
        lblTargetPort = new JLabel("Target Port:");
        lblUserName = new JLabel("Username:");
        try {
            lblYourAddress = new JLabel("My Address: " + InetAddress.getLocalHost().getHostAddress() + ":?");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        lblServerStatus = new JLabel("Server: Disconnected");
        lblMessages = new JLabel("Messages:");

        txtFieldTargetIP = new JTextField("127.0.0.1", 16);
        txtFieldTargetPort = new JTextField("5500", 10);
        txtFieldUsername = new JTextField("Ana", 25);
        txtFieldMessageBox = new JTextField();

        txtAreaMessage = new JTextArea(16, 60);

        btnConnect = new JButton("Connect");
        btnSend = new JButton("Send");
        btnClear = new JButton("Clear");

        btnSend.setEnabled(false);

        scroll = new JScrollPane(txtAreaMessage);

        // Set font size
        lblTargetIP.setFont(lblTargetIP.getFont().deriveFont(16.0f));
        lblTargetPort.setFont(lblTargetPort.getFont().deriveFont(16.0f));
        lblUserName.setFont(lblUserName.getFont().deriveFont(16.0f));
        lblYourAddress.setFont(lblYourAddress.getFont().deriveFont(16.0f));
        lblServerStatus.setFont(lblServerStatus.getFont().deriveFont(16.0f));
        lblMessages.setFont(lblMessages.getFont().deriveFont(16.0f));

        txtFieldTargetIP.setFont(txtFieldTargetIP.getFont().deriveFont(16.0f));
        txtFieldTargetPort.setFont(txtFieldTargetPort.getFont().deriveFont(16.0f));
        txtFieldUsername.setFont(txtFieldUsername.getFont().deriveFont(16.0f));
        txtFieldMessageBox.setFont(txtFieldMessageBox.getFont().deriveFont(16.0f));

        txtAreaMessage.setFont(txtAreaMessage.getFont().deriveFont(16.0f));

        // Set scroll bar policy
        txtAreaMessage.setEditable(false);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Add components to panel
        jPanel.add(lblTargetIP, setGBC(0, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblTargetPort, setGBC(1, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblUserName, setGBC(2, 0, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(txtFieldTargetIP, setGBC(0, 1, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(txtFieldTargetPort, setGBC(1, 1, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(txtFieldUsername, setGBC(2, 1, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnConnect, setGBC(3, 1, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(lblYourAddress, setGBC(0, 2, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerStatus, setGBC(2, 2, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(lblMessages, setGBC(0, 3, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnClear, setGBC(3, 3, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(scroll, setGBC(0, 4, 0, 5, 4, 1, 10, 5, 10, 0));

        jPanel.add(txtFieldMessageBox, setGBC(0, 5, 0, 5, 3, 1, 0, 5, 0, 0));
        jPanel.add(btnSend, setGBC(3, 5, 0, 5, 1, 1, 0, 5, 0, 0));

        jFrame.add(jPanel);
        jFrame.setVisible(true);

   
        // Add action listeners
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAreaMessage.setText("");
            }
        });

        txtFieldMessageBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = txtFieldMessageBox.getText();
                    if (!message.isEmpty()) {
                        updateMessageLog("You: " + message);
                        client.sendMessage(message);
                        txtFieldMessageBox.setText("");
                    }
                }
            }
        });

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtFieldUsername.getText();
                int port = Integer.parseInt(txtFieldTargetPort.getText());
                // Check for connection
                try {
                    socket = new Socket("localhost", port);
                    lblServerStatus.setText("Server: Connected");
                    btnConnect.setEnabled(false);
                    btnSend.setEnabled(true);
                    txtFieldMessageBox.setEnabled(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("Connection lost. Retrying...");
                    lblServerStatus.setText("Server: Disconnected");
                    btnConnect.setEnabled(true);
                    btnSend.setEnabled(false);
                    txtFieldMessageBox.setEnabled(false);
                }
                client = new Client(socket, username);
                client.sendMessage(username);
                client.listenForMessage();

                // get output port
                try {
                    lblYourAddress.setText("My Address: " + InetAddress.getLocalHost().getHostAddress() + ":"
                            + socket.getLocalPort());
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = txtFieldMessageBox.getText();
                if (!message.isEmpty()) {
                    updateMessageLog("You: " + message);
                    client.sendMessage(message);
                    txtFieldMessageBox.setText("");
                }
            }
        });

    }

    public static void updateMessageLog(String message) {
        txtAreaMessage.append(message + "\n");
    }

    public static void main(String[] args) {
        new ClientUI();
    }
}