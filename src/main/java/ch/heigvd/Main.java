package ch.heigvd;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new HangmanGame()).execute(args);
        System.exit(exitCode);
    }
}

@CommandLine.Command(name = "Hangman",
        subcommands = {ClientSubcommand.class, ServerSubcommand.class},
        description = "Run a game of Hangman")
class HangmanGame {

}

abstract class HangmanCommand implements Runnable {
    @CommandLine.Option(names={"-p", "--port"}, description = "defines a specific port to use")
    protected int portNumber = 12345;
}

@CommandLine.Command(name = "server",
        description = "starts a server for a game of Hangman")
class ServerSubcommand extends HangmanCommand {
    @CommandLine.Option(names={"-t", "--thread"}, description = "defines a number of thread to use for the server")
    private int threadNumber = 5;

    @Override
    public void run() {
        System.out.println("server with " + threadNumber + " threads on " + portNumber);
    }
}


@CommandLine.Command(name = "client",
        description = "Starts a client for a game of Hangman")
class ClientSubcommand extends HangmanCommand {
    @Override
    public void run() {
        System.out.println("client on " + portNumber);
    }
}