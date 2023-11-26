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

                // Player variables
                String word = "";
                StringBuilder guessedWord = new StringBuilder();
                int livesNbr = 0;

                // Main game loop
                while (true) {
                    String[] command = in.readLine().split(" ");
                    System.out.println("[SERVER] command received : " + command[0]);
                    String reply = "";
                    switch (command[0]) {
                        case START:
                            if (command.length != 3) {
                                // Incorrect command length
                                reply = FAIL + " " + PARAM_ERROR;
                            } else if (!Utils.isNumeric(command[1]) || (Integer.parseInt(command[1])) <= 0) {
                                // Incorrect parameters type
                                reply = FAIL + " " + PARAM_ERROR;
                            } else {
                                // Player variables setup
                                livesNbr = MAX_LIVES_NBR;
                                guessedWord = new StringBuilder();

                                // Generate a word
                                int length = Integer.parseInt(command[1]);
                                word = Utils.findWord(Utils.Language.valueOf(command[2]), length).toUpperCase();
                                System.out.println("[Server] Random word generated with " + length + " letters: " + word);
                                for (int i = 0; i < length; ++i) {
                                    // Replace non-alphabetic characters in the word sent to the client and
                                    // Hide alphabetic characters
                                    guessedWord.append(!Character.isAlphabetic(word.charAt(i)) ?
                                            word.charAt(i) : '_');
                                }

                                reply = CORRECT + " " + guessedWord;
                            }

                            break;
                        case GUESS:
                            if (command.length != 2 || command[1].isEmpty()) {
                                reply = FAIL + " " + PARAM_ERROR;
                            } else if (command[1].length() == 1 && Character.isAlphabetic(command[1].charAt(0))) {
                                // Guess letter
                                boolean found = false;
                                for (int i = 0; i < word.length(); ++i) {
                                    if (command[1].equals(String.valueOf(word.charAt(i)))) {
                                        guessedWord.setCharAt(i, command[1].charAt(0));
                                        found = true;
                                    }
                                }

                                if (found) {
                                    reply = guessedWord.toString().equals(word) ?
                                            WIN + " " + word :
                                            CORRECT + " " + guessedWord;
                                } else {
                                    reply = --livesNbr <= 0 ?
                                            LOSE + " " + word :
                                            WRONG + " " + guessedWord;
                                }
                            } else {
                                // Guess word
                                if (command[1].equals(word)) {
                                    reply = WIN + " " + word;
                                } else {
                                    reply = --livesNbr <= 0 ?
                                            LOSE + " " + word :
                                            WRONG + " " + guessedWord;
                                }
                            }

                            break;
                        case EXIT:
                            System.out.println("[Server] Client request 'EXIT', closing connection...");
                            socket.close();
                            return;
                        default:
                            reply = FAIL + " " + UNKNOWN_COMMAND;
                            break;
                    }

                    out.write(reply + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("[Server] exception: " + e);
            }
        }
    }
}
