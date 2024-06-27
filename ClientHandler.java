
import java.io.*;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientList = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;
    private String clientIP;
    private int clientPort;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = bufferedReader.readLine();
            this.clientIP = socket.getInetAddress().getHostAddress();
            this.clientPort = socket.getPort();
            // Add client to list
            clientList.add(this);
            broadcastMessage("@Server: " + this.clientUserName + " has joined the chat", true);
            ServerUI.logAppend("#-Log: " + this.clientUserName + " has joined the chat");
        } catch (IOException e) {
            e.printStackTrace();
            releaseResources(socket, bufferedReader, bufferedWriter);
        }
        updateUIConnectedClients();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getClientName() {
        return this.clientUserName;
    }

    public void listenForMessage(String clientUserName, String clientIP, int clientPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromClient;
                while (!socket.isClosed()) {
                    try {
                        messageFromClient = bufferedReader.readLine();
                        if (messageFromClient == null)
                            throw new IOException();

                        broadcastMessage(clientUserName + ": " + messageFromClient, false);
                        ServerUI.logAppend("#" + clientUserName + ": " + messageFromClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                        releaseResources(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String messageToSend, String target) {
        for (ClientHandler clientHandler : clientList) {
            try {
                if (clientHandler.clientUserName.equals(target)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                releaseResources(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
    }

    public void broadcastMessage(String messageToSend, boolean isServerMessage) {
        for (ClientHandler clientHandler : clientList) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName) || isServerMessage) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                releaseResources(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
    }

    // Refresh connected clients list
    public void updateUIConnectedClients() {
        ServerUI.clearClients();
        for (ClientHandler clientProcessor : clientList) {
            ServerUI.addClients(clientProcessor.getClientName());
        }
        ServerUI.setClientsCount(clientList.size());

        if (clientList.size() > 0) {
            ServerUI.setClientAvailable(true);
        } else {
            ServerUI.setClientAvailable(false);
        }
    }

    private void releaseResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null)
                bufferedReader.close();

            if (bufferedWriter != null)
                bufferedWriter.close();

            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            ServerUI.logAppend("#-Log: Error releasing resources.");
            return;
        }
        clientList.remove(this);
        broadcastMessage("@Server: " + this.clientUserName + " has left.", true);
        ServerUI.logAppend("#-Log: " + this.clientUserName + " has left.");
        updateUIConnectedClients();
    }

    @Override
    public void run() {
        listenForMessage(this.clientUserName, this.clientIP, this.clientPort);
    }

}
