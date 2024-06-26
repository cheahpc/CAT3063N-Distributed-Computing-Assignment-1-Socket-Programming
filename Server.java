import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    private ServerSocket serverSocket;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("#-Log\t: New client connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return !serverSocket.isClosed();
    }
    // public static void main(String[] args) throws IOException {
        // ServerSocket serverSocket = new ServerSocket(5000);
        // Server server = new Server(serverSocket);
        // server.startServer();
    // }
}