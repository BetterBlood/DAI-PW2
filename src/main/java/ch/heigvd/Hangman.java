package ch.heigvd;

import picocli.CommandLine;

import java.util.Scanner;

public abstract class Hangman implements Runnable {
    @CommandLine.Option(names = {"-p", "--port"}, description = "defines a specific port to use for communication")
    protected int portNum = 12345;

    public void startRepl() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the REPL. Enter 'exit' to end.");

        while (true) {
            System.out.print("Enter a command: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase(Utils.EXIT)) {
                System.out.println("Exiting REPL. Goodbye!");
                break;
            }

            // Process the user input (you can replace this with your command processing logic)
            processReplCommand(userInput);
        }

        scanner.close();
    }

    protected abstract void processReplCommand(String command);
}
