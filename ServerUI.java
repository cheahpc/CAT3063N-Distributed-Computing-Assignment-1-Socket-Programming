import java.awt.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.*;
import java.io.*;

public class ServerUI extends JFrame {
    private ServerSocket serverSocket;
    private Socket socket = null;
    private DataInputStream in = null;

    private int connectionCount = 0;

    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel lblSeverIP, lblServerIPValue, lblServerPort, lblTotalClients, lblMessageTo, lblMessage, lblServerLog;
    private JTextField txtFieldServerPort, txtFieldTotalClients, txtFieldMessage;
    private JButton btnChange, btnSend;
    private JComboBox<String> cmbClients;
    private JTextArea txtAreaServerLog;
    private JScrollPane scroll;
    private GridBagConstraints gbConstraints;

    private GridBagConstraints setGBC(int x, int y, int px, int py, int gWidth, int gHeight, int to, int le, int bo, int ri) {
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

    public ServerUI() {
        jFrame = new JFrame("Chat Server");
        jFrame.setSize(900, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        gbConstraints = new GridBagConstraints();

        // Create components
        lblSeverIP = new JLabel("Server IP:");
        lblServerPort = new JLabel("Server Port:");
        lblTotalClients = new JLabel("Total Clients:");
        lblMessageTo = new JLabel("Message To:");
        lblMessage = new JLabel("Message:");
        lblServerLog = new JLabel("Server Log:");
        try {
            lblServerIPValue = new JLabel(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            lblServerIPValue = new JLabel("Unknown");
        }

        txtFieldServerPort = new JTextField(6);
        txtFieldTotalClients = new JTextField(2);
        txtFieldMessage = new JTextField(20);

        txtAreaServerLog = new JTextArea(16, 60);

        cmbClients = new JComboBox<String>();

        btnChange = new JButton("Change");
        btnSend = new JButton("Send");

        scroll = new JScrollPane(txtAreaServerLog);

        // Set text field properties
        txtFieldTotalClients.setEditable(false);
        txtFieldTotalClients.setEnabled(false);
        txtAreaServerLog.setEditable(false);

        // Set font size
        lblSeverIP.setFont(lblSeverIP.getFont().deriveFont(16.0f));
        lblServerPort.setFont(lblServerPort.getFont().deriveFont(16.0f));
        lblTotalClients.setFont(lblTotalClients.getFont().deriveFont(16.0f));
        lblMessageTo.setFont(lblMessageTo.getFont().deriveFont(16.0f));
        lblServerIPValue.setFont(lblServerIPValue.getFont().deriveFont(16.0f));
        txtFieldServerPort.setFont(txtFieldServerPort.getFont().deriveFont(16.0f));
        txtFieldTotalClients.setFont(txtFieldTotalClients.getFont().deriveFont(16.0f));
        cmbClients.setFont(cmbClients.getFont().deriveFont(16.0f));
        btnChange.setFont(btnChange.getFont().deriveFont(16.0f));
        lblMessage.setFont(lblMessage.getFont().deriveFont(16.0f));
        txtFieldMessage.setFont(txtFieldMessage.getFont().deriveFont(16.0f));
        btnSend.setFont(btnSend.getFont().deriveFont(16.0f));
        lblServerLog.setFont(lblServerLog.getFont().deriveFont(16.0f));
        txtAreaServerLog.setFont(txtAreaServerLog.getFont().deriveFont(16.0f));

        // Set scroll bar policy
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Add components to panel
        jPanel.add(lblSeverIP, setGBC(0, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerIPValue, setGBC(1, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerPort, setGBC(2, 0, 0, 5, 1, 1, 0, 15, 0, 0));
        jPanel.add(txtFieldServerPort, setGBC(3, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnChange, setGBC(4, 0, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(lblTotalClients, setGBC(0, 1, 0, 5, 1, 1, 10, 0, 0, 0));
        jPanel.add(txtFieldTotalClients, setGBC(1, 1, 0, 5, 1, 1, 10, 0, 0, 0));
        jPanel.add(lblMessageTo, setGBC(2, 1, 0, 5, 1, 1, 10, 15, 0, 0));
        jPanel.add(cmbClients, setGBC(3, 1, 0, 5, 2, 1, 10, 5, 0, 0));

        jPanel.add(lblMessage, setGBC(0, 2, 0, 5, 1, 1, 10, 5, 0, 0));
        jPanel.add(txtFieldMessage, setGBC(1, 2, 0, 5, 3, 1, 10, 5, 0, 0));
        jPanel.add(btnSend, setGBC(4, 2, 0, 5, 1, 1, 10, 5, 0, 0));

        jPanel.add(lblServerLog, setGBC(0, 3, 0, 5, 1, 1, 10, 5, 0, 0));

        jPanel.add(scroll, setGBC(0, 4, 0, 10, 5, 1, 10, 5, 0, 0));

        jFrame.add(jPanel);
        jFrame.setVisible(true);

        // Add action listeners
        btnChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updateLog("#-Log: Changing server port...");
                if (txtFieldServerPort.getText().equals("") || Integer.parseInt(txtFieldServerPort.getText()) > 65535
                        || Integer.parseInt(txtFieldServerPort.getText()) < 1024) {
                    txtFieldServerPort.setText("5500");
                    updateLog("!-Error: Port number must be between 1024 and 65535");
                    updateLog("#-Log: Default server port used (5500)");
                }
                // Check if server is already started
                if (server.isRunning()) {
                    // Close server
                    server.closeEverything
                
                }
                try {
                    serverSocket = new ServerSocket(5000);
                    Server server = new Server(serverSocket);
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        // Add key listeners
        txtFieldServerPort.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                // Limit only numbers up to 6 digits
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
                        || txtFieldServerPort.getText().length() >= 5) {
                    evt.consume();
                }
            }

        });
    }

    public void clearLog() {
        txtAreaServerLog.setText("");
    }

    public void updateLog(String message) {
        txtAreaServerLog.append(message + "\n");
    }

    public void updateTotalClients(int count) {
        txtFieldTotalClients.setText(Integer.toString(count));
    }

    public static void main(String[] args) {
        new ServerUI();
    }
}