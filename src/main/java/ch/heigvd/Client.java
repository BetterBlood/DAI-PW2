package ch.heigvd;

import picocli.CommandLine;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Hangman")
public class Client extends Hangman {
    private static final String HOST = "localhost";
    private static final int DEFAULT_WORD_LENGTH = 7;
    private static final String DEFAULT_LANGUAGE = "EN";

    private static boolean isAlphabetic(String s) {
        return s.matches("[a-zA-Z]+");
    }

    private static boolean isNumeric(String s) {
        return s.matches("\\d+");
    }

    private static boolean isValidLanguage(String input) {
        try {
            Utils.Language.valueOf(input.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void run() {
        try (
                Socket socket = new Socket(HOST, portNum);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            System.out.println("[Client] connected to " + HOST + ":" + portNum);
            Scanner scanner = new Scanner(System.in);
            String word = "";
            int nbrLives = -1;
            System.out.println("[Client] Type Exit to quit");
            while (true) {
                System.out.print("[Client] Enter a command: ");
                String command = scanner.nextLine().toUpperCase();

                // Check input
                String[] arguments = command.split(" ");
                if (arguments.length == 0) {
                    continue;
                }

                if (word.isEmpty() && arguments[0].equalsIgnoreCase(START)) {
                    String defaultStart = START + " " + DEFAULT_WORD_LENGTH + " " + DEFAULT_LANGUAGE;
                    nbrLives = LIVES_NBR;
                    switch (arguments.length) {
                        case 3:
                            if (!isNumeric(arguments[1]) || !isValidLanguage(arguments[2])) {
                                command = defaultStart;
                            }
                            // We do nothing if all is fine
                            break;
                        case 2:
                            if (!isNumeric(arguments[1])) {
                                command = defaultStart;
                            } else {
                                command = START + " " + DEFAULT_LANGUAGE;
                            }
                            break;
                        case 1:
                            // If an incorrect number of argument are given in argument, those are discarded and default ones are enforced
                            command = defaultStart;
                            break;
                    }

                } else if (!word.isEmpty() && arguments[0].equalsIgnoreCase(SUBMIT) && arguments.length == 2) {
                    if (!isAlphabetic(arguments[1])) {
                        continue;
                    }
                } else if (arguments[0].equalsIgnoreCase(EXIT)) {
                    out.write(EXIT + "\n");
                    out.flush();
                    System.out.println("[Client] Exiting Hangman Game. Goodbye!");
                    break;
                } else {
                    // If no matching commands are found, print a help message
                    System.out.println("""
                            [Help]
                            'exit' to quit
                            'start [nbLetters] [EN-FR]' to start a game
                            'submit [letter]' to guess a letter
                            'submit [word]' to guess a word""");
                    continue;
                }

                // Sends the command
                System.out.println("[Client] sending command to server " + HOST + ":" + portNum + ": " + command);
                out.write(command + "\n");
                out.flush();

                // Handles server answer
                String[] answer = in.readLine().split(" ");
                if (answer[0].equalsIgnoreCase(FAIL)) {
                    System.out.println("[Client] server error : could not generate a word with that length, please try another length or another language.");
                } else if (answer[0].equalsIgnoreCase(CORRECT)) {
                    if (word.isEmpty()) {
                        System.out.println("[Client]\n" + nbrLives + " lives\n" + answer[1]);
                    } else {
                        System.out.println("[Client] correct letter ! \n" + nbrLives + " lives\n" + answer[1]);
                    }

                    word = answer[1];
                } else if (answer[0].equalsIgnoreCase(WRONG)) {
                    System.out.println("[Client] incorrect letter ! \n" + --nbrLives + " lives\n" + word);
                } else if (answer[0].equalsIgnoreCase(WIN)) {
                    System.out.println("[Client] Congratulations ! The correct word was " + answer[1]);
                } else if (answer[0].equalsIgnoreCase(LOSE)) {
                    System.out.println("[Client] Game over... The correct word was " + answer[1]);
                } else {
                    System.out.println("[Client] non supported reply from the server " + answer[0]);
                }
            }

            socket.close();
            scanner.close();
            System.out.println("[Client] closing connection");

        } catch (IOException e) {
            System.out.println("[Client] exception: " + e);
        }
    }
}
