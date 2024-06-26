import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Socket socket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
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
            clientHandler.endServer();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getConnectedClients() {
        return ClientHandler.clientHandlers.size();
    }

    public void removeClient(String clientName) {
        clientHandler.removeClient(clientName);
    }

    // public static void main(String[] args) throws IOException {
    // ServerSocket serverSocket = new ServerSocket(5000);
    // Server server = new Server(serverSocket);
    // server.startServer();
    // }
}