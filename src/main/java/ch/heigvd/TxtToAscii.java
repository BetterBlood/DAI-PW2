package ch.heigvd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TxtToAscii {

    public static void main(String[] args) {
        String inputFilePath = "files\\french_chrplr.txt"; // french_chrplr
        String outputFilePath = "files\\french_ascii_chrplr.txt"; // french_ascii_chrplr

        try {
            replaceNonASCIICharacters(inputFilePath, outputFilePath);
            System.out.println("Modification terminée avec succès.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la modification du fichier : " + e.getMessage());
        }
    }

    public static void replaceNonASCIICharacters(String inputFilePath, String outputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath, StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, StandardCharsets.UTF_8))) {

            String line;
            String previous = "";
            while ((line = reader.readLine()) != null) {
                String modifiedLine = line.replaceAll("[^\\x00-\\x7F]", "");
                if (!modifiedLine.equals(previous))
                {
                    writer.write(modifiedLine);
                    writer.newLine();
                    previous = modifiedLine;
                }
            }
        }
    }
}