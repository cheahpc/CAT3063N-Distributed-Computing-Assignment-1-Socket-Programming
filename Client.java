import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = username;
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        // Prevent the main thread from blocking
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        Socket socket = null;
        Client client = null;
        while (true) {
            // Check for connection
            try {
                socket = new Socket("localhost", 5500);
                client = new Client(socket, username);
            } catch (IOException e) {
                e.printStackTrace();
                client.closeEverything(socket, client.bufferedReader, client.bufferedWriter);
                System.out.println("Connection lost. Retrying...");
                // Sleep for 5 seconds before retrying
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                continue;
            }
            client.listenForMessage();
            client.sendMessage();
        }
    }
}