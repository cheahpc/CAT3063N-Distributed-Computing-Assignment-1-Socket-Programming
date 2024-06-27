import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import java.text.SimpleDateFormat;

public class ServerUI extends JFrame {
    private Server server;
    private ServerSocket serverSocket;

    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel lblServerIP, lblServerIPValue, lblServerPort, lblTotalClients, lblMessageTo, lblMessage,
            lblServerLog, lblServerStatus;
    private static JLabel lblServerStatusValue, lblActiveClientsCount;
    private static JTextField txtFieldMessage, txtFieldServerPort;
    private JButton btnClear;
    private static JButton btnStartStop, btnSend, btnRemove;
    private static JComboBox<String> cmbClients;
    private static JTextArea txtAreaServerLog;
    private JScrollPane scroll;

    private static boolean serverOnline = false;

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

    private void uiCreate() {
        jFrame = new JFrame("PC.Chat (Server) Developed by: @Pin Chee Ver1.0");

        jPanel = new JPanel();

        lblServerIP = new JLabel("Server IP:");
        lblServerPort = new JLabel("Server Port:");
        lblTotalClients = new JLabel("Total Clients:");
        lblMessageTo = new JLabel("Target :");
        lblMessage = new JLabel("Message:");
        lblServerLog = new JLabel("Server Log:");
        lblServerStatus = new JLabel("Server Status:");
        lblServerStatusValue = new JLabel("Offline");
        lblServerStatusValue.setForeground(java.awt.Color.RED);
        lblActiveClientsCount = new JLabel("0");
        lblServerIPValue = new JLabel(Server.getServerIP());
        txtFieldServerPort = new JTextField(15);
        txtFieldMessage = new JTextField(43);

        txtAreaServerLog = new JTextArea(16, 60);

        scroll = new JScrollPane(txtAreaServerLog);

        cmbClients = new JComboBox<String>();

        btnStartStop = new JButton("Start");
        btnSend = new JButton("Send");
        btnRemove = new JButton("Remove");
        btnClear = new JButton("Clear");

    }

    private void uiSettings() {
        jFrame.setSize(950, 650);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jPanel.setLayout(new GridBagLayout());
        jPanel.setBackground(java.awt.Color.lightGray);

        cmbClients.addItem("-All-");

        txtAreaServerLog.setEditable(false);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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

        btnStartStop.setFont(btnStartStop.getFont().deriveFont(16.0f));
        btnSend.setFont(btnSend.getFont().deriveFont(16.0f));
        btnRemove.setFont(btnRemove.getFont().deriveFont(16.0f));
        btnClear.setFont(btnClear.getFont().deriveFont(16.0f));

        // Set remove and stop button to red
        btnRemove.setBackground(java.awt.Color.RED);
        btnRemove.setForeground(java.awt.Color.WHITE);
        btnStartStop.setBackground(java.awt.Color.blue);
        btnStartStop.setForeground(java.awt.Color.WHITE);

        // Set button properties
        setClientAvailable(false);
        setServerOnline(false);
    }

    private void uiAdd() {
        // Add components to panel
        jPanel.add(lblServerIP, setGBC(0, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerIPValue, setGBC(1, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(lblServerPort, setGBC(2, 0, 0, 5, 1, 1, 0, 15, 0, 0));
        jPanel.add(txtFieldServerPort, setGBC(3, 0, 0, 5, 1, 1, 0, 5, 0, 0));
        jPanel.add(btnStartStop, setGBC(4, 0, 0, 5, 2, 1, 0, 5, 0, 0));

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
    }

    private void actSend() {
        String message = txtFieldMessage.getText();
        String target = (String) cmbClients.getSelectedItem();
        // Check if message is empty
        if (message.equals(""))
            return;

        if (target.equals("-All-"))
            server.broadcastMessage("@Server: " + message, true);
        else
            server.sendMessage("@Server: " + message, target);

        logAppend("@Server: " + txtFieldMessage.getText());
        txtFieldMessage.setText("");
    }

    public ServerUI() {

        uiCreate();
        uiSettings();
        uiAdd();

        jFrame.add(jPanel);
        jFrame.setVisible(true);
        // Add action listeners
        btnStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (serverOnline) {
                    server.endServer();
                } else {
                    String sPort = txtFieldServerPort.getText();

                    // Check port field
                    if (sPort.equals("")) {
                        logAppend("#-Log: Default server port used (8888)");
                        txtFieldServerPort.setText("8888");
                        sPort = "8888";
                    } else if (Integer.parseInt(sPort) > 65535 || Integer.parseInt(sPort) < 1024) {
                        logAppend("#-Log: Please enter a valid port number (1024-65535)");
                        return;
                    }

                    // Start Server
                    // Get server IP address
                    String sIP = Server.getServerIP();

                    // Step 1: Check if server socket already exists
                    if (serverSocket != null && serverSocket.isBound()) {
                        server.endServer();
                        setServerOnline(false);
                        logAppend("#-Log: Server ended");
                    }

                    // Step 2: Try to create server socket (Essentially starting the server)
                    ServerUI.logAppend("#-Log: Starting server...");
                    try {
                        serverSocket = new ServerSocket(Integer.parseInt(sPort));
                        ServerUI.setServerOnline(true);
                        ServerUI.logAppend("#-Log: Socket created successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        ServerUI.logAppend("#-Log: Port number already in use. Please try another port number.");
                        return;
                    }

                    // Step 3: Handle connections
                    server = new Server(serverSocket);
                    server.initializeServer();

                    // Step 4: Update UI
                    ServerUI.logAppend("#-Log: Server started on:" + sIP + ":" + sPort);
                    return;
                }
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String clientName = (String) cmbClients.getSelectedItem();
                logAppend("#-Log: Remove operation Started.");
                if (clientName.equals("-All-"))
                    server.removeAllClients();
                else
                    server.removeClient(clientName);
            }
        });

        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                actSend();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                logClear();
            }
        });

        txtFieldMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    actSend();
                }
            }
        });

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

    public static void setClientAvailable(boolean status) {
        if (status) {
            txtFieldMessage.setEnabled(true);
            btnRemove.setEnabled(true);
            btnSend.setEnabled(true);
            btnRemove.setBackground(java.awt.Color.RED);
        } else {
            txtFieldMessage.setEnabled(false);
            btnRemove.setEnabled(false);
            btnSend.setEnabled(false);
            btnRemove.setBackground(null);
        }
    }

    public static void setServerOnline(boolean status) {
        if (status) {
            serverOnline = true;
            lblServerStatusValue.setText("Online");
            lblServerStatusValue.setForeground(java.awt.Color.GREEN);
            txtFieldServerPort.setEnabled(false);
            btnStartStop.setText("Stop");
            btnStartStop.setBackground(java.awt.Color.RED);
        } else {
            serverOnline = false;
            lblServerStatusValue.setText("Offline");
            lblServerStatusValue.setForeground(java.awt.Color.RED);
            txtFieldServerPort.setEnabled(true);
            btnStartStop.setText("Start");
            btnStartStop.setBackground(java.awt.Color.BLUE);
        }
    }

    public static void setClientsCount(int count) {
        lblActiveClientsCount.setText(Integer.toString(count));
    }

    public static void logClear() {
        ServerUI.txtAreaServerLog.setText("");
    }

    public static void logAppend(String message) {
        // Get date and time up to milliseconds
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);
        ServerUI.txtAreaServerLog.append(formattedTimestamp + " " + message + "\n");
    }

    public static void addClients(String client) {
        cmbClients.addItem(client);
    }

    public static void clearClients() {
        cmbClients.removeAllItems();
        cmbClients.addItem("-All-");
    }

    public static void main(String[] args) {
        new ServerUI();
    }
}