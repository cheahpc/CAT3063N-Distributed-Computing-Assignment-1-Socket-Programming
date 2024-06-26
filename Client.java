import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private String userName;

    public Client(Socket socket, String username) {
        try {
            Client.socket = socket;
            Client.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Client.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = username;
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        // Prevent the main thread from blocking
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        // Prevent the main thread from blocking
        if (socket.isConnected()) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection lost. Please reconnect.");
        }

    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                        ClientUI.updateMessageLog(msgFromGroupChat);
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        System.out.println("Connection lost. Retrying...");
                        ClientUI.lblServerStatus.setText("Server: Disconnected");
                        ClientUI.btnConnect.setEnabled(true);
                        ClientUI.btnSend.setEnabled(false);
                        ClientUI.txtFieldMessageBox.setEnabled(false);
                        break;
                    }
                }
            }
        }).start();
    }

    public static void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    // public static void main(String[] args) throws IOException {
    // Scanner scanner = new Scanner(System.in);
    // System.out.print("Enter username: ");
    // String username = scanner.nextLine();
    // Client client;
    // while (true) {
    // // Check for connection
    // try {
    // socket = new Socket("localhost", 5500);
    // client = new Client(socket, username);
    // } catch (ConnectException e) {
    // e.printStackTrace();
    // closeEverything(socket, bufferedReader, bufferedWriter);
    // System.out.println("Connection lost. Retrying...");
    // // Sleep for 5 seconds before retrying
    // try {
    // Thread.sleep(2000);
    // } catch (InterruptedException ex) {
    // ex.printStackTrace();
    // }
    // continue;
    // }
    // client.listenForMessage();

    // client.sendMessage();
    // }
    // }
}