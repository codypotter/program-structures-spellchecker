import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * The SpellCheck program takes in a dictionary document and a test file. It outputs
 * any typos from the test file.
 *
 * The SpellCheck class holds all methods for the spell checker program.
 *
 * @author Cody Potter
 * @version 1.0
 * @since 2018-03-07
 */
public class SpellCheck {
    private static HashSet<String> dictionaryHashSet = new HashSet<>();
    private static Map<String, Integer> typoTracker = new TreeMap<>();
    private static int totalTypos = 0;
    private static int totalWords = 0;

    public static void main(String args[]) {
        System.out.println("Cody Potter");
        String spellcheckFilename;
        String dictionaryFilename;

        if (args.length == 2) {
            spellcheckFilename = args[0];
            dictionaryFilename = args[1];
        } else if (args.length == 1) {
            spellcheckFilename = args[0];
            dictionaryFilename = "words.txt";
        } else if (args.length == 0) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Please enter the file to spell check:");
            spellcheckFilename = reader.nextLine();
            dictionaryFilename = "words.txt";
        } else {
            System.err.println("ERROR: Invalid arguments. Usage: spellcheck [spellcheck filename] [dictionary filename]");
            return;
        }

        if (isInvalidFilename(spellcheckFilename) || isInvalidFilename(dictionaryFilename)) {
            System.err.println("ERROR: Invalid filename. Exiting...");
            return;
        }

        File spellcheckFile = new File(spellcheckFilename);
        File dictionaryFile = new File(dictionaryFilename);

        if (!spellcheckFile.exists() || !dictionaryFile.exists()) {
            System.err.println("ERROR: File not found. Exiting...");
            return;
        }

        System.out.println("Spellcheck filename: " + spellcheckFilename);
        System.out.println("Dictionary filename: " + dictionaryFilename);

        scanFiles(spellcheckFile, dictionaryFile);

        for (String typo : typoTracker.keySet()) {
            System.out.println(typo + " (" + typoTracker.get(typo) + ")");
        }
        System.out.println("\nTotal words: " + totalWords);
        System.out.println("Total typos: " + totalTypos);
        System.out.println("Thanks for using SpellCheck. Exiting...");
    }

    private static void scanFiles(File spellcheckFile, File dictionaryFile) {
        try {
            Scanner spellcheckScanner = new Scanner(spellcheckFile);
            Scanner dictionaryScanner = new Scanner(dictionaryFile);

            System.out.println("Scanning dictionary, please wait (this could take a few seconds)...");
            while (dictionaryScanner.hasNextLine()) {
                dictionaryHashSet.add(dictionaryScanner.nextLine());
            }
            System.out.println("Dictionary scanned.\n");

            System.out.println("Spell checking file, please wait (this could take a few seconds)...");
            while (spellcheckScanner.hasNext()) {
                String word = spellcheckScanner.next();
                word = word.replaceAll("[^a-zA-Z]","-");
                word = editForWeirdCharacters(word);
                if (!word.equals("")) { checkDictionaryAgainst(word); }
            }
            System.out.println("Spell checking complete. Outputting errors:");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean isInvalidFilename(String filename) {
        return !filename.endsWith(".txt") || filename.length() < 5;
    }

    private static String editForWeirdCharacters(String word) {
        if (word.contains("-")) {
            String[] words = word.split("-");
            for (String splitWord : words) { checkDictionaryAgainst(splitWord); }
            return "";
        }
        return word.trim();
    }

    private static void checkDictionaryAgainst(String word) {
        totalWords++;
        String lowercaseWord = word.toLowerCase();
        if (!dictionaryHashSet.contains(lowercaseWord)) {
            totalTypos++;
            if (typoTracker.containsKey(lowercaseWord)) {
                int count = typoTracker.get(lowercaseWord);
                count++;
                typoTracker.put(lowercaseWord, count);
            } else { typoTracker.put(lowercaseWord, 1); }
        }
    }
}
