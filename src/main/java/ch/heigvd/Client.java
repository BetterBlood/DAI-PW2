package ch.heigvd;

import picocli.CommandLine;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Hangman")
public class Client extends Hangman {
    private static final String HOST = "localhost";

    @Override
    public void run() {
        try (
                Socket socket = new Socket(HOST, portNum);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            System.out.println("[Client] connected to " + HOST + ":" + portNum);
            Scanner scanner = new Scanner(System.in);
            boolean run = true;
            while (run) {
                System.out.print("[Client] Enter a command: ");
                String command = scanner.nextLine();
                System.out.println("[Client] sending textual data to server " + HOST + ":" + portNum + ": " + command);
                //TODO check if commands sent are correct
                out.write(command + "\n");
                out.flush();
                String[] answer = in.readLine().split(" ");
                switch (answer[0]) {
                    case "FAIL":
                        //TODO handle fail
                        System.out.println("[Client] server error : could not generate a word with that length");
                        break;
                    case "CORRECT":
                        //TODO display word
                        System.out.println("[Client] correct ! " + answer[1]);
                        break;
                    case "WRONG":
                        //TODO decrement lives
                        System.out.println("[Client] incorrect ! ");
                        break;
                    case "EXIT":
                        System.out.println("[Client] Exiting Hangman Game. Goodbye!");
                        run = false;
                        break;
                    default:
                        System.out.println("[Client] non supported reply from the server " + answer[0]);
                        break;
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
