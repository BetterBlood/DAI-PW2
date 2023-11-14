package ch.heigvd;

import picocli.CommandLine;

@CommandLine.Command(name = "server",
        description = "starts a server for a game of Hangman")
public class Server extends Hangman {
    @CommandLine.Option(names = {"-t", "--thread"}, description = "defines a number of thread to use for the server")
    private int threadNbr = 5;

    @Override
    public void run() {
        System.out.println("server with " + threadNbr + " threads on " + portNum);
        startRepl();

        // dictionary test
        int length = 4;
        String word = Utils.findWord(Utils.Language.EN, length);
        System.out.println("Random word with " + length + " letters: " + word);
    }

    @Override
    protected void processReplCommand(String command) {
        System.out.println("Executing command: " + command);
    }
}
