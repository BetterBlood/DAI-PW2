package ch.heigvd;

import picocli.CommandLine;

import java.util.Scanner;

public abstract class Hangman implements Runnable {
    protected static final int LIVES_NBR = 7;

    // Messages sent to the server
    protected static final String START = "START";
    protected static final String SUBMIT = "SUBMIT";
    protected static final String EXIT = "EXIT";

    // Messages sent to the client
    protected static final String FAIL = "FAIL";
    protected static final String CORRECT = "CORRECT";
    protected static final String WRONG = "WRONG";
    protected static final String LOSE = "LOSE";
    protected static final String WIN = "WIN";

    @CommandLine.Option(names = {"-p", "--port"}, description = "defines a specific port to use for communication")
    protected int portNum = 12345;
}
