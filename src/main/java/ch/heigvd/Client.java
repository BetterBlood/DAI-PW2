package ch.heigvd;

import picocli.CommandLine;

@CommandLine.Command(name = "client",
        description = "Starts a client for a game of Hangman")
public class Client extends Hangman {
    @Override
    public void run() {
        System.out.println("client on " + portNum);
        startRepl();
    }

    @Override
    protected void processReplCommand(String command) {
        System.out.println("Executing command: " + command);

    }
}
