package ch.heigvd;

import picocli.CommandLine;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommandLine.Command(name = "server",
        description = "starts a server for a game of Hangman")
public class Server extends Hangman {
    @CommandLine.Option(names = {"-t", "--thread"}, description = "defines a number of thread to use for the server")
    private int threadNbr = 5;

    @Override
    public void run() {
        System.out.println("server with " + threadNbr + " threads on " + portNum);
        ExecutorService executor = null;

        // Start the server
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            executor = Executors.newFixedThreadPool(threadNbr);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("[Server] exception: " + e);
        }
    }

    static class ClientHandler implements Runnable {

        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    socket; // This allows to use try-with-resources with the socket
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            ) {
                System.out.println("[Server] new client connected from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                String word = "";
                while (true) {
                    String[] command = in.readLine().split(" ");
                    switch (command[0]) {
                        case "START":
                            // Generate a word
                            int length = Integer.parseInt(command[1]);
                            String languageCode = command[2];
                            //TODO handle languageCode to generate word
                            word = Utils.findWord(Utils.Language.EN, length);
                            System.out.println("[Server] Random word generated with " + length + " letters: " + word);

                            String s = new String(new char[length]).replace('\0', '_');
                            out.write("CORRECT " + s + "\n");
                            out.flush();
                            break;
                        case "SUBMIT":
                            //TODO check word and reply with correct, wrong, win, lose
                            out.write("CORRECT _____\n");
                            out.flush();
                            break;
                        case "EXIT":
                            System.out.println("[Server] closing connection");
                            socket.close();
                            return;
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] exception: " + e);
            }
        }
    }
}
