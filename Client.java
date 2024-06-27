import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            Client.socket = socket;
            Client.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Client.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            releaseResources(socket, bufferedReader, bufferedWriter);
        }
    }

    public static String getClientIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public int getClientPort() {
        return socket.getLocalPort();
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (!socket.isClosed()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();

                        if (msgFromGroupChat == null)
                            throw new IOException();

                        ClientUI.logAppend(msgFromGroupChat);
                    } catch (IOException e) {
                        e.printStackTrace();
                        releaseResources(socket, bufferedReader, bufferedWriter);
                        ClientUI.logAppend("#-System: Disconnected from server.");
                        ClientUI.setServerConnected(false);
                        break;
                    }
                }
            }
        }).start();
    }

    public static void releaseResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}