
import java.io.*;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;
    private String clientIP;
    private int clientPort;

    public void listenForMessage(String clientUserName, String clientIP, int clientPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromClient;
                while (socket.isConnected()) {
                    try {
                        messageFromClient = bufferedReader.readLine();
                        broadcastMessage("@" + clientUserName + ": " + messageFromClient, false);
                        // update log
                        ServerUI.updateLog(
                                clientIP + ":" + clientPort + " @" + clientUserName + ": " + messageFromClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public String getClientName() {
        return this.clientUserName;
    }

    public void sendMessage(String messageToSend, String target) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUserName.equals(target)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
    }

    public void broadcastMessage(String messageToSend, boolean isServerMessage) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName) || isServerMessage) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
        System.out.println(messageToSend);
    }

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = bufferedReader.readLine();
            this.clientIP = socket.getInetAddress().getHostAddress();
            this.clientPort = socket.getPort();
            // Add client to list
            clientHandlers.add(this);
            broadcastMessage("%-Server: @" + this.clientUserName + " has joined the chat", true);
            ServerUI.updateLog("#-Log: " + this.clientIP + ":" + this.clientPort + " @" + this.clientUserName
                    + " has joined the chat");

            refreshConnectedClients();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Refresh connected clients list
    private void refreshConnectedClients() {
        ServerUI.clearClients();
        for (ClientHandler clientHandler : clientHandlers) {
            ServerUI.addClients(clientHandler.getClientName());
        }
        ServerUI.updateActiveClientsCount(clientHandlers.size());
    }

    public void removeClient(String clientName) {
        for (ClientHandler clientHandler : clientHandlers) {
            ServerUI.updateLog("LoopX");
            if (clientHandler.clientUserName.equals(clientName)) {
                clientHandlers.remove(clientHandler);
                // clientHandler.closeEverything(clientHandler.socket, clientHandler.bufferedReader,
                        // clientHandler.bufferedWriter);
                ServerUI.updateLog("#-Log: " + clientHandler.clientIP + ":" + clientHandler.clientPort + " @" + clientHandler.clientUserName
                        + " has left the chat");
                break;
            }
        }
        refreshConnectedClients();
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("%-Server: @" + this.clientUserName + " has left the chat", true);
        ServerUI.updateLog(
                "#-Log: " + this.clientIP + ":" + this.clientPort + " @" + this.clientUserName + " has left the chat");
        refreshConnectedClients();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null)
                bufferedReader.close();

            if (bufferedWriter != null)
                bufferedWriter.close();

            if (socket != null && !socket.isClosed())
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getBufferedReader() {
        return this.bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return this.bufferedWriter;
    }

    @Override
    public void run() {
        listenForMessage(this.clientUserName, this.clientIP, this.clientPort);
    }

}
