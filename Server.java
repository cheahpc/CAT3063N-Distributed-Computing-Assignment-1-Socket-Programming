import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Socket socket;
    public static boolean serverRunning = false;

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
                        System.out.println("#-Log\t: New client connected");
                        clientHandler = new ClientHandler(socket);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                        Server.serverRunning = true;
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
        try {
            serverSocket.close();
            for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
                clientHandler.removeClientHandler();
            }
            // clientHandler.removeClient(clientHandler.getClientName());

            Server.serverRunning = false;
            System.out.println("#-Log: Server closed");
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