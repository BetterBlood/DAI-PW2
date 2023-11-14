package ch.heigvd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utils {
    public enum Language {
        EN,
        FR
    }

    private static final HashMap<Language, String> dictionaries = new HashMap<>();
    private static final String EN_DICTIONARY = "english_words_jlawler.txt";
    private static final String FR_DICTIONARY = "french_words_taknok.txt";

    // Messages sent to the server
    public static final String CONNECT = "CONNECT";
    public static final String START = "START";
    public static final String SUBMIT = "SUBMIT";

    // Messages sent to the client
    public static final String FAIL = "FAIL";
    private static final String CORRECT = "CORRECT";
    public static final String WRONG = "WRONG";
    public static final String LOSE = "LOSE";
    public static final String WIN = "WIN";

    // Messages sent in both directions
    public static final String EXIT = "EXIT";

    static {
        dictionaries.put(Language.EN, EN_DICTIONARY);
        dictionaries.put(Language.FR, FR_DICTIONARY);
    }

    public static String findWord(Language language, int numberOfLetters) {
        try {
            List<String> words = loadWords(dictionaries.get(language));
            return getRandomWord(words, numberOfLetters);
        } catch (IOException e) {
            System.err.println("Error while trying to open the dictionary file \n" + e);
            return "";
        }
    }

    private static List<String> loadWords(String filename) throws IOException {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        }

        return words;
    }

    private static String getRandomWord(List<String> words, int numberOfLetters) {
        Random random = new Random();
        List<String> matchingWords = new ArrayList<>();

        for (String word : words) {
            if (word.length() == numberOfLetters) {
                matchingWords.add(word);
            }
        }

        if (matchingWords.isEmpty()) {
            System.out.println("No words found with " + numberOfLetters + " letters.");
            return null;
        }

        return matchingWords.get(random.nextInt(matchingWords.size()));
    }
}
