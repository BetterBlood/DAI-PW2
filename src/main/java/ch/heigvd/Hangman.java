package ch.heigvd;

import picocli.CommandLine;

public abstract class Hangman implements Runnable {
    protected static final int MAX_LIVES_NBR = 7;

    // Messages sent to the server
    protected static final String START = "START";
    protected static final String GUESS = "GUESS";
    protected static final String EXIT = "EXIT";

    // Messages sent to the client
    protected static final String FAIL = "FAIL";
    protected static final String CORRECT = "CORRECT";
    protected static final String WRONG = "WRONG";
    protected static final String LOSE = "LOSE";
    protected static final String WIN = "WIN";

    // Errors reasons
    protected static final String PARAM_ERROR = "PARAM_ERROR";
    protected static final String UNKNOWN_COMMAND = "UNKNOWN_COMMAND";

    @CommandLine.Option(names = {"-p", "--port"}, description = "defines a specific port to use for communication")
    protected int portNum = 12345;
}
