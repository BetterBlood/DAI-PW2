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
            StringBuilder answer = new StringBuilder();
            int livesNbr = MAX_LIVES_NBR;

            try (
                    socket; // This allows to use try-with-resources with the socket
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            ) {
                System.out.println("[Server] new client connected from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                String word = "";
                while (true) {
                    String[] command = in.readLine().split(" ");
                    System.out.println("[SERVER] command received : " + command[0]);
                    switch (command[0]) {
                        case START:
                            if (command.length != 3)
                            {
                                out.write(FAIL + " " + PARAM_ERROR + "\n");
                                out.flush();
                                break;
                            }
                            else if (!Utils.isNumeric(command[1]) || (Integer.parseInt(command[1])) <= 0)
                            {
                                out.write(FAIL + " " + PARAM_ERROR + "\n");
                                out.flush();
                                break;
                            }
                            // Generate a word
                            int length = Integer.parseInt(command[1]);
                            livesNbr = MAX_LIVES_NBR;
                            word = Utils.findWord(Utils.Language.valueOf(command[2]), length).toUpperCase();
                            System.out.println("[Server] Random word generated with " + length + " letters: " + word);

                            answer = new StringBuilder();
                            for (int i = 0; i < length; ++i)
                            {
                                if (!Character.isAlphabetic(word.charAt(i)))
                                {// remplace les charactère non alphabétique dans le mots envoyé au client
                                    answer.append(word.charAt(i));
                                }
                                else
                                { // masque les charactères alphabetics
                                    answer.append('_');
                                }
                            }
                            out.write(CORRECT + " " + answer + "\n");
                            out.flush();
                            break;

                        case GUESS:
                            if (command.length != 2 || command[1].isEmpty())
                            {
                                out.write(FAIL + " " + PARAM_ERROR + "\n");
                                out.flush();
                            }
                            else if (command[1].length() == 1 && Character.isAlphabetic(command[1].charAt(0))) // guess letter
                            {
                                boolean found = false;
                                for (int i = 0; i < word.length(); ++i)
                                {
                                    if (command[1].equals(String.valueOf(word.charAt(i))))
                                    {
                                        answer.setCharAt(i,command[1].charAt(0));
                                        found = true;
                                    }
                                }
                                if (found)
                                {
                                    if (answer.toString().equals(word))
                                    {
                                        out.write(WIN + " " + word + "\n");
                                        out.flush();
                                    }
                                    else
                                    {
                                        out.write(CORRECT + " " + answer + "\n");
                                        out.flush();
                                    }
                                }
                                else
                                {
                                    if (--livesNbr <= 0)
                                    {
                                        out.write(LOSE + " " + word + "\n");
                                        out.flush();
                                    }
                                    else
                                    {
                                        out.write(WRONG + " " + answer + "\n");
                                        out.flush();
                                    }
                                }
                            }
                            else // guess word
                            {
                                if (command[1].equals(word))
                                {
                                    out.write(WIN + " " + word + "\n");
                                    out.flush();
                                }
                                else
                                {
                                    if (--livesNbr <= 0)
                                    {
                                        out.write(LOSE + " " + word + "\n");
                                        out.flush();
                                    }
                                    else
                                    {
                                        out.write(WRONG + " " + answer + "\n");
                                        out.flush();
                                    }
                                }
                            }
                            break;

                        case EXIT:
                            System.out.println("[Server] Client request 'EXIT', closing connection...");
                            socket.close();
                            return;

                        default:
                            out.write(FAIL + " " + UNKNOWN_COMMAND + "\n");
                            out.flush();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] exception: " + e);
            }
        }
    }
}
