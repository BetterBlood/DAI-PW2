package ch.heigvd;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new HangmanGame()).execute(args);
        System.exit(exitCode);
    }
}

@CommandLine.Command(name = "Hangman",
        subcommands = {Client.class, Server.class, CommandLine.HelpCommand.class},
        description = "Run a game of Hangman")
class HangmanGame {
}
