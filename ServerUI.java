import java.awt.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;

public class ServerUI extends JFrame {
    private ServerSocket serverSocket;
    private Server server;

    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel lblServerIP, lblServerIPValue, lblServerPort, lblTotalClients, lblMessageTo, lblMessage, lblServerLog,
            lblServerStatus, lblServerStatusValue;
    private JTextField txtFieldServerPort;
    private static JLabel lblActiveClientsCount;
    private JTextField txtFieldMessage;
    private JButton btnStart, btnStop, btnSend, btnRemove, btnClear;
    private static JComboBox<String> cmbClients;
    private static JTextArea txtAreaServerLog;
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

    public ServerUI() {
        jFrame = new JFrame("Chat Server");
        jFrame.setSize(1100, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());

        // Create components
        lblServerIP = new JLabel("Server IP:");
        lblServerPort = new JLabel("Server Port:");
        lblTotalClients = new JLabel("Total Clients:");
        lblMessageTo = new JLabel("Target :");
        lblMessage = new JLabel("Message:");
        lblServerLog = new JLabel("Server Log:");
        lblServerStatus = new JLabel("Server Status:");
        lblServerStatusValue = new JLabel("Offline");
        lblActiveClientsCount = new JLabel("0");
        try {
            lblServerIPValue = new JLabel(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            lblServerIPValue = new JLabel("Unknown");
        }

        txtFieldServerPort = new JTextField(6);
        txtFieldMessage = new JTextField(20);

        txtAreaServerLog = new JTextArea(16, 60);

        cmbClients = new JComboBox<String>();

        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
        btnSend = new JButton("Send");
        btnRemove = new JButton("Remove");
        btnClear = new JButton("Clear");

        scroll = new JScrollPane(txtAreaServerLog);

        // Set text field properties
        txtAreaServerLog.setEditable(false);

        // Set font size
        lblServerIP.setFont(lblServerIP.getFont().deriveFont(16.0f));
        lblServerPort.setFont(lblServerPort.getFont().deriveFont(16.0f));
        lblTotalClients.setFont(lblTotalClients.getFont().deriveFont(16.0f));
        lblMessageTo.setFont(lblMessageTo.getFont().deriveFont(16.0f));
        lblServerIPValue.setFont(lblServerIPValue.getFont().deriveFont(16.0f));
        lblMessage.setFont(lblMessage.getFont().deriveFont(16.0f));
        lblServerLog.setFont(lblServerLog.getFont().deriveFont(16.0f));
        lblServerStatus.setFont(lblServerStatus.getFont().deriveFont(16.0f));
        lblServerStatusValue.setFont(lblServerStatusValue.getFont().deriveFont(16.0f));
        lblActiveClientsCount.setFont(lblActiveClientsCount.getFont().deriveFont(16.0f));

        txtFieldServerPort.setFont(txtFieldServerPort.getFont().deriveFont(16.0f));
        txtFieldMessage.setFont(txtFieldMessage.getFont().deriveFont(16.0f));
        txtAreaServerLog.setFont(txtAreaServerLog.getFont().deriveFont(16.0f));
        cmbClients.setFont(cmbClients.getFont().deriveFont(16.0f));

        btnStart.setFont(btnStart.getFont().deriveFont(16.0f));
        btnStop.setFont(btnStop.getFont().deriveFont(16.0f));
        btnSend.setFont(btnSend.getFont().deriveFont(16.0f));
        btnRemove.setFont(btnRemove.getFont().deriveFont(16.0f));
        btnClear.setFont(btnClear.getFont().deriveFont(16.0f));

        // Set scroll bar policy
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Add components to panel
        jPanel.add(lblServerIP, setGBC(0, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerIPValue, setGBC(1, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerPort, setGBC(2, 0, 0, 5, 1, 1, 0, 15, 0, 0));
        jPanel.add(txtFieldServerPort, setGBC(3, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnStart, setGBC(4, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnStop, setGBC(5, 0, 0, 5, 1, 1, 0, 5, 0, 0));

        jPanel.add(lblTotalClients, setGBC(0, 1, 0, 5, 1, 1, 10, 0, 0, 0));
        jPanel.add(lblActiveClientsCount, setGBC(1, 1, 0, 5, 1, 1, 10, 0, 0, 0));
        jPanel.add(lblMessageTo, setGBC(2, 1, 0, 5, 1, 1, 10, 15, 0, 0));
        jPanel.add(cmbClients, setGBC(3, 1, 0, 5, 2, 1, 10, 5, 0, 0));
        jPanel.add(btnRemove, setGBC(5, 1, 0, 5, 1, 1, 10, 5, 0, 0));

        jPanel.add(lblMessage, setGBC(0, 2, 0, 5, 1, 1, 10, 5, 0, 0));
        jPanel.add(txtFieldMessage, setGBC(1, 2, 0, 5, 4, 1, 10, 5, 0, 0));
        jPanel.add(btnSend, setGBC(5, 2, 0, 5, 1, 1, 10, 5, 0, 0));

        jPanel.add(lblServerLog, setGBC(0, 3, 0, 5, 1, 1, 10, 5, 0, 0));
        jPanel.add(lblServerStatus, setGBC(2, 3, 0, 5, 1, 1, 10, 5, 0, 0));
        jPanel.add(lblServerStatusValue, setGBC(3, 3, 0, 5, 1, 1, 10, 5, 0, 0));
        jPanel.add(btnClear, setGBC(5, 3, 0, 5, 1, 1, 10, 5, 0, 0));

        jPanel.add(scroll, setGBC(0, 4, 0, 10, 6, 1, 10, 5, 0, 0));

        jFrame.add(jPanel);
        jFrame.setVisible(true);

        // Add action listeners
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Server.serverRunning = true;
                // Check if server is already started
                if (serverSocket != null && !serverSocket.isClosed()) {
                    // Close server
                    server.endServer();
                    // serverSocket = null;
                    lblServerStatusValue.setText("Offline");
                    updateLog("#-Log: Server stopped");
                }

                updateLog("#-Log: Initializing...");
                if (txtFieldServerPort.getText().equals("")
                        || Integer.parseInt(txtFieldServerPort.getText()) > 65535
                        || Integer.parseInt(txtFieldServerPort.getText()) < 1024) {
                    txtFieldServerPort.setText("5500");
                    updateLog("#-Log: Default server port used (5500)");
                }
                // Start server
                try {
                     serverSocket = new ServerSocket(Integer.parseInt(txtFieldServerPort.getText()));
                } catch (NumberFormatException | IOException e) {
                    e.printStackTrace();
                }

                server = new Server(serverSocket);
                server.startServer();
                updateLog("#-Log: Server started on: " + lblServerIPValue.getText() + ":"
                        + txtFieldServerPort.getText());
                lblServerStatusValue.setText("Online");
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clearLog();
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // Close Server
                if (serverSocket != null && !serverSocket.isClosed()) {
                    lblServerStatusValue.setText("Offline");
                    updateLog("#-Log: Server stopped");
                    server.endServer();
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

    public static void clearLog() {
        ServerUI.txtAreaServerLog.setText("");
    }

    public static void updateLog(String message) {
        // Get date and time up to milliseconds
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);
        ServerUI.txtAreaServerLog.append(formattedTimestamp + " " + message + "\n");
    }

    public static void addClients(String client) {
        cmbClients.addItem(client);
    }

    public static void removeClients(String client) {
        cmbClients.removeItem(client);
    }

    public static void clearClients() {
        cmbClients.removeAllItems();
        cmbClients.addItem("-All-");
    }

    public static void updateActiveClientsCount(int count) {
        lblActiveClientsCount.setText(Integer.toString(count));
    }

    public void updateTotalClients(int count) {
        lblActiveClientsCount.setText(Integer.toString(count));
    }

    public static void main(String[] args) {
        new ServerUI();
    }
}