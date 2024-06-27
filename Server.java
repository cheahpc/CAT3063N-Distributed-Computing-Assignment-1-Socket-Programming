import java.net.*;
import java.io.IOException;

public class Server {

    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Socket socket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public void initializeServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!serverSocket.isClosed()) {
                        socket = serverSocket.accept();
                        clientHandler = new ClientHandler(socket);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String messageToSend, String target) {
        clientHandler.sendMessage(messageToSend, target);
    }

    public void broadcastMessage(String messageToSend, boolean isServerMessage) {
        clientHandler.broadcastMessage(messageToSend, isServerMessage);
    }

    public void endServer() {
        // Close all client handlers
        if (clientHandler != null)
            removeAllClients();
        try {
            serverSocket.close();
            ServerUI.setServerOnline(false);
        } catch (IOException e) {
            e.printStackTrace();
            ServerUI.logAppend("#-Log: Error closing server.");
            ServerUI.setServerOnline(true);
        }
    }

    public void removeAllClients() {
        for (ClientHandler activeClient : ClientHandler.clientList) {
            sendMessage("@Server: You have been removed from the chat.", activeClient.getClientName());
            try {
                activeClient.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClientHandler.clientList.clear();
        clientHandler.updateUIConnectedClients();
    }

    public void removeClient(String clientName) {
        for (ClientHandler activeClient : ClientHandler.clientList) {
            if (activeClient.getClientName().equals(clientName)) {
                sendMessage("@Server: You have been removed from the chat.", activeClient.getClientName());
                ClientHandler.clientList.remove(activeClient);
                try {
                    activeClient.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        clientHandler.updateUIConnectedClients();
    }

}