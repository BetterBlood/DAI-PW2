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

    private static boolean isValidLanguage(String input) {
        try {
            Utils.Language.valueOf(input.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static final String[] HANGMAN_PICS = {
            "  +---+\n" +
            "  |   |\n" +
            "      |\n" +
            "      |\n" +
            "      |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            "      |\n" +
            "      |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            "  |   |\n" +
            "      |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            " /|   |\n" +
            "      |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            " /|\\  |\n" +
            "      |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            " /|\\  |\n" +
            " /    |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            " /|\\  |\n" +
            " / \\  |\n" +
            "      |\n" +
            "=========",

            "  +---+\n" +
            "  |   |\n" +
            "  O   |\n" +
            " |||  |\n" +
            " | |  |\n" +
            "      |\n" +
            "========="
    };

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

                if (arguments[0].equals(START)) {
                    word = "";
                    String defaultStart = START + " " + DEFAULT_WORD_LENGTH + " " + DEFAULT_LANGUAGE;
                    nbrLives = MAX_LIVES_NBR;
                    switch (arguments.length) {
                        case 3:
                            if ((!Utils.isNumeric(arguments[1]) && (Integer.valueOf(arguments[1])) > 0) || !isValidLanguage(arguments[2])) {
                                command = defaultStart;
                            }
                            // We do nothing if all is fine
                            break;
                        case 2:
                        case 1:
                            // If an incorrect number of argument are given in argument, those are discarded and default ones are enforced
                            command = defaultStart;
                            break;
                    }

                } else if (!word.isEmpty() && arguments[0].equalsIgnoreCase(GUESS) && arguments.length == 2) {
                    if (!Utils.isWord(arguments[1])) {
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
                            'guess [letter]' to guess a letter
                            'guess [word]' to guess a word""");
                    continue;
                }

                // Sends the command
                System.out.println("[Client] sending command to server " + HOST + ":" + portNum + ": " + command);
                out.write(command + "\n");
                out.flush();

                // Handles server answer
                String[] answer = in.readLine().split(" ");
                if (answer[0].equalsIgnoreCase(FAIL)) {
                    if (arguments[0].equalsIgnoreCase(START))
                    {
                        System.out.println("[Client] server error '" + answer[1] + "': could not generate a word with that length, please try another length or another language.");
                    }
                    else if (arguments[0].equalsIgnoreCase(GUESS))
                    {
                        System.out.println("[Client] server error : '" + answer[1] + "'");
                    }
                    else
                    {
                        System.out.println("[Client] '" + answer[1] + "' non supported command send to server : " + arguments[0]);
                    }
                } else if (answer[0].equalsIgnoreCase(CORRECT)) {
                    if (word.isEmpty()) {
                        System.out.println("[Client]\n" + nbrLives + " lives\n" + answer[1]);
                    } else {
                        System.out.println("[Client] correct letter ! \n" + nbrLives + " lives\n" + answer[1]);
                    }
                    System.out.println(HANGMAN_PICS[MAX_LIVES_NBR - nbrLives]);
                    word = answer[1];
                } else if (answer[0].equalsIgnoreCase(WRONG)) {
                    System.out.println("[Client] incorrect letter ! \n" + --nbrLives + " lives\n" + word);
                    System.out.println(HANGMAN_PICS[MAX_LIVES_NBR - nbrLives]);
                } else if (answer[0].equalsIgnoreCase(WIN)) {
                    System.out.println("[Client] Congratulations ♥_♥ ! The correct word was " + answer[1]);
                } else if (answer[0].equalsIgnoreCase(LOSE)) {
                    System.out.println("[Client] Game over... X_X The correct word was " + answer[1]);
                    System.out.println(HANGMAN_PICS[MAX_LIVES_NBR - --nbrLives]);
                } else {
                    System.out.println("[Client] non supported reply from the server " + answer[0]);
                    System.out.println(HANGMAN_PICS[MAX_LIVES_NBR - nbrLives]);
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
