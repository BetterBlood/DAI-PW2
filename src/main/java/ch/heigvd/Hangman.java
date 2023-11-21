package ch.heigvd;

import picocli.CommandLine;

import java.util.Scanner;

public abstract class Hangman implements Runnable {
    @CommandLine.Option(names = {"-p", "--port"}, description = "defines a specific port to use for communication")
    protected int portNum = 12345;
}
