
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public void listenForMessage(String clientUserName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromClient;
                while (socket.isConnected()) {
                    try {
                        messageFromClient = bufferedReader.readLine();
                        broadcastMessage(clientUserName + ": " + messageFromClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;  
                    }
                }
            }
        }).start();
    }

    // Todo - either broadcast or specify client
    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)) {
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
            clientHandlers.add(this);
            broadcastMessage("%-Server: " + this.clientUserName + " has joined the chat");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("%-Server: " + clientUserName + " has left the chat");
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

    @Override
    public void run() {

        listenForMessage(this.clientUserName);
        sendMessage();

    }

}
